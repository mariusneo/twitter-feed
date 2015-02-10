package mg.twitter.feed.jobs.countwords.job;

import mg.twitter.feed.jobs.countwords.domain.CountWordsTweet;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweetRepository;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweetStatus;
import mg.twitter.feed.jobs.countwords.service.CountTweetWordsService;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class CountTweetsWordsJob implements Job {
    @Autowired
    private CountWordsTweetRepository countWordsTweetRepository;

    @Autowired
    private CountTweetWordsService countTweetWordsService;

    @Value("${count.tweets.words.job.bucket.size}")
    private int limit;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Slf4JStopWatch stopWatch = new Slf4JStopWatch();

        String tag = "";
        try {
            String triggerKey = context.getTrigger().getKey().toString();
            String jobKey = context.getJobDetail().getKey().toString();
            List<CountWordsTweet> countWordsTweets = countWordsTweetRepository.findNewCountWordsTweets(limit);

            List<Long> tweetIds = countWordsTweets.stream().map(t -> t.getTweetId()).collect(Collectors.toList());

            if (tweetIds.size() > 0) {
                countWordsTweetRepository.updateStatus(tweetIds, CountWordsTweetStatus.PROCESSING);

                for (CountWordsTweet countWordsTweet : countWordsTweets) {
                    countTweetWordsService.processCountWordsTweet(countWordsTweet);
                }
            }

            tag = String.format("CountTweetsWordsJob.success] job[%s] trigger[%s] processed[%d] limit[%d", jobKey,
                    triggerKey, countWordsTweets.size(), limit);
        } catch (Exception e) {
            tag = String.format("CountTweetsWordsJob.failure] exception[%s", e.getMessage());
            throw e;
        } finally {
            stopWatch.stop(tag);
        }
    }
}
