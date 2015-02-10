package mg.twitter.feed.jobs.countwords.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ReleasePendingTweetsJob implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("release pending tweets job");
    }
}
