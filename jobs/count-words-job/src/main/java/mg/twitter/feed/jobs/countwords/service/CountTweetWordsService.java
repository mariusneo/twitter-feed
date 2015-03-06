package mg.twitter.feed.jobs.countwords.service;

import mg.twitter.feed.domain.Tweet;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweet;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
public class CountTweetWordsService {
    public static final  int MINUTES_COUNT = 5;

    @Autowired
    private CountWordsTweetRepository countWordsTweetRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Value("${redis.datasource.url}")
    private String redisUrl;

    @Async
    @Transactional
    public void processCountWordsTweet(CountWordsTweet countWordsTweet) {
        Long tweetId = countWordsTweet.getId();

        Tweet tweet = tweetRepository.findOne(tweetId);

        Map<String, Integer> word2countMap = new HashMap<>();

        // processing
        System.out.println(tweet.getText());

        for (String word : tweet.getText().split(" ")){
            if (word.trim().length()==0) continue;
            int count = 0;
            if (word2countMap.containsKey(word)){
                count = word2countMap.get(word);
            }
            word2countMap.put(word, count + 1);
        }


        int tweetsSetIndex = Calendar.getInstance().get(Calendar.MINUTE) % MINUTES_COUNT;

        for (Map.Entry<String,Integer> entry : word2countMap.entrySet()) {
            Jedis jedis = new Jedis(redisUrl);
            jedis.zincrby("tweets.minutes:" + tweetsSetIndex, entry.getValue(), entry.getKey());
            jedis.zincrby("tweets.totals", entry.getValue(), entry.getKey());
        }


        // in OK case, delete the entry
        countWordsTweetRepository.delete(tweetId);
    }
}
