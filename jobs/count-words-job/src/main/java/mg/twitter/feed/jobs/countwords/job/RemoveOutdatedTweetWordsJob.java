package mg.twitter.feed.jobs.countwords.job;

import mg.twitter.feed.jobs.countwords.service.CountTweetWordsService;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.Calendar;
import java.util.Set;

public class RemoveOutdatedTweetWordsJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveOutdatedTweetWordsJob.class);

    private static int BUCKET_LIMIT = 100;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Slf4JStopWatch stopWatch = new Slf4JStopWatch();
        int outdatedTweetsSetIndex = (Calendar.getInstance().get(Calendar.MINUTE) + 1)
                % (CountTweetWordsService.MINUTES_COUNT + 1);
        String outdatedWordsSetName = "words.minutes:" + outdatedTweetsSetIndex;

        String tag = "";
        try {
            String triggerKey = context.getTrigger().getKey().toString();
            String jobKey = context.getJobDetail().getKey().toString();
            Jedis counterJedis = jedisPool.getResource();
            long itemsCount = 0;

            try {
                itemsCount = counterJedis.zcard(outdatedWordsSetName);
            } finally {
                jedisPool.returnResource(counterJedis);
            }

            // use itemsCount -1 in order to avoid iterating 2 times for a item count equal to BUCKET_LIMIT
            int index = 0;
            for (int i = 0; i <= (itemsCount - 1) / BUCKET_LIMIT; i++) {
                long endIndex = index + BUCKET_LIMIT > itemsCount ? itemsCount - 1 : index + BUCKET_LIMIT - 1;
                Set<Tuple> range = counterJedis.zrangeWithScores("words.minutes:" + outdatedTweetsSetIndex, index,
                        endIndex);

                range.parallelStream().forEach(tuple -> {
                    Jedis wordJedis = jedisPool.getResource();
                    try {
                        Double score = wordJedis.zscore("words.totals", tuple.getElement());
                        if (score == null) return;
                        wordJedis.zincrby("words.totals", -tuple.getScore(), tuple.getElement());
                    } finally {
                        jedisPool.returnResource(wordJedis);
                    }
                });
                index += BUCKET_LIMIT;
            }

            Jedis removerJedis = jedisPool.getResource();
            try {
                // remove from totals all the elements which have the value less or equal to 0
                Long removedCount = removerJedis.zremrangeByScore("words.totals", "-inf", "0");
                LOGGER.info("Removed from words.totals queue " + removedCount + " elements");

                removerJedis.del(outdatedWordsSetName);
            } finally {
                jedisPool.returnResource(removerJedis);
            }

            tag = String.format("RemoveOutdatedTweetWordsJob.success] job[%s] trigger[%s] processed[%d", jobKey,
                    triggerKey, itemsCount);
        } catch (Exception e) {
            tag = String.format("RemoveOutdatedTweetWordsJob.failure] exception[%s", e.getMessage());
            throw e;
        } finally {
            stopWatch.stop(tag);
        }
    }
}
