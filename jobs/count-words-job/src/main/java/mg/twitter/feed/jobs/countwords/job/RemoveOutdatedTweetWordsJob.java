package mg.twitter.feed.jobs.countwords.job;

import mg.twitter.feed.jobs.countwords.service.CountTweetWordsService;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Calendar;
import java.util.Set;

public class RemoveOutdatedTweetWordsJob implements Job {

    private static int BUCKET_LIMIT = 100;

    @Value("${redis.datasource.url}")
    private String redisUrl;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Slf4JStopWatch stopWatch = new Slf4JStopWatch();
        int outdatedTweetsSetIndex = (Calendar.getInstance().get(Calendar.MINUTE) + 1 ) % CountTweetWordsService
                .MINUTES_COUNT;
        String outdatedTweetsSetName = "tweets.minutes:" + outdatedTweetsSetIndex;

        String tag = "";
        try {
            String triggerKey = context.getTrigger().getKey().toString();
            String jobKey = context.getJobDetail().getKey().toString();
            Jedis jedis = new Jedis(redisUrl);
            long itemsCount = jedis.zcard(outdatedTweetsSetName);

            // use itemsCount -1 in order to avoid iterating 2 times for a item count equal to BUCKET_LIMIT
            int index = 0;
            for (int i = 0; i <= (itemsCount - 1) / BUCKET_LIMIT; i++) {
                long endIndex = index + BUCKET_LIMIT > itemsCount ? itemsCount - 1 : index + BUCKET_LIMIT - 1;
                Set<Tuple> range = jedis.zrangeWithScores("tweets.minutes:" + outdatedTweetsSetIndex, index, endIndex);

                range.parallelStream().forEach(tuple -> {
                    Jedis jedis1 = new Jedis(redisUrl);
                    Double score = jedis1.zscore("tweets.totals", tuple.getElement());
                    if (score == null) return;
                    if (score == tuple.getScore()) {
                        jedis1.zrem("tweets.totals", tuple.getElement());
                    } else {
                        jedis1.zincrby("tweets.totals", -tuple.getScore(), tuple.getElement());
                    }
                });
                index += BUCKET_LIMIT;
            }

            jedis.del(outdatedTweetsSetName);

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
