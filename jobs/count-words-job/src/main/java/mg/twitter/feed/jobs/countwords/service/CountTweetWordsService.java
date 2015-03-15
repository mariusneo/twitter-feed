package mg.twitter.feed.jobs.countwords.service;

import mg.twitter.feed.domain.Tweet;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweet;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class CountTweetWordsService {

    /**
     * In redis there will be (MINUTES_COUNT + 1)  buckets available, and the bucket corresponding to the next minute
     * will be removed within {@link mg.twitter.feed.jobs.countwords.job.RemoveOutdatedTweetWordsJob} at the
     * beginning of each minute.
     */
    public static final int MINUTES_COUNT = 5;


    private static final Logger LOGGER = LoggerFactory.getLogger(CountTweetWordsService.class);

    private static Set<String> STOP_WORDS = null;

    static {
        try {
            STOP_WORDS = loadStopWords("stopwords.txt");
        } catch (Exception e) {
            throw new RuntimeException("Couldn't initialize the stop words.", e);
        }
    }

    @Autowired
    private CountWordsTweetRepository countWordsTweetRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private JedisPool jedisPool;

    private static Set<String> loadStopWords(String filename) throws Exception {
        Set<String> stopWords = new HashSet<>();
        URL url = CountTweetWordsService.class.getClassLoader().getResource(filename);
        Path path = new File(url.toURI()).toPath();
        Stream<String> lines = Files.lines(path);
        lines.forEach(s -> stopWords.add(s));
        lines.close();
        return stopWords;
    }

    @Async
    @Transactional
    public void processCountWordsTweet(CountWordsTweet countWordsTweet) {
        Long tweetId = countWordsTweet.getId();

        Tweet tweet = tweetRepository.findOne(tweetId);

        Map<String, Integer> word2countMap = new HashMap<>();

        long now = new Date().getTime();
        long tweetCreatedAt = tweet.getCreatedAt().getTime();
        if ((now - tweetCreatedAt) / (60 * 1000) < MINUTES_COUNT) {
            // take into account only the tweets created in the last minutes for processing
            LOGGER.info(tweet.getText());

            for (String word : tweet.getText().split(" ")) {
                word = word.toLowerCase().trim().replaceAll(":#", "");
                word = word.replaceAll("\\.+$", "");
                if (word.trim().length() < 2 || STOP_WORDS.contains(word)) continue;
                int count = 0;
                if (word2countMap.containsKey(word)) {
                    count = word2countMap.get(word);
                }
                word2countMap.put(word, count + 1);
            }


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tweet.getCreatedAt());
            int tweetsSetIndex = calendar.get(Calendar.MINUTE) % (MINUTES_COUNT + 1);

            Jedis jedis = jedisPool.getResource();
            try {
                for (Map.Entry<String, Integer> entry : word2countMap.entrySet()) {
                    // Use a transaction for performing multiple ordered set update operations
                    Transaction tx = jedis.multi();
                    tx.zincrby("words.minutes:" + tweetsSetIndex, entry.getValue(), entry.getKey());
                    tx.zincrby("words.totals", entry.getValue(), entry.getKey());
                    tx.exec();
                }
            } finally {
                /// ... it's important to return the Jedis instance to the pool once you've finished using it
                jedisPool.returnResource(jedis);
            }
        }

        // in OK case, delete the entry
        countWordsTweetRepository.delete(tweetId);
    }
}
