package mg.twitter.feed.jobs.countwords.config;

import mg.twitter.feed.jobs.common.AutowiringSpringBeanJobFactory;
import mg.twitter.feed.jobs.countwords.job.CountTweetsWordsJob;
import mg.twitter.feed.jobs.countwords.job.RemoveOutdatedTweetWordsJob;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Value("${org.quartz.dataSource.quartzDS.URL}")
    private String quartzDataSourceUrl;
    @Value("${org.quartz.dataSource.quartzDS.user}")
    private String quartzDataSourceUser;
    @Value("${org.quartz.dataSource.quartzDS.password}")
    private String quartzDataSourcePassword;
    @Value("${org.quartz.dataSource.quartzDS.maxConnections}")
    private String quartzDataSourceMaxConnections;
    @Value("${org.quartz.dataSource.quartzDS.validationQuery}")
    private String quartzDataSourceValidationQuery;
    @Value("${org.quartz.dataSource.quartzDS.driver}")
    private String quartzDataSourceDriver;

    @Value("${org.quartz.scheduler.instanceName}")
    private String quartzSchedulerInstanceName;
    @Value("${org.quartz.scheduler.instanceId}")
    private String quartzSchedulerInstanceId;
    @Value("${org.quartz.scheduler.jmx.export}")
    private String quartzSchedulerJmxExport;
    @Value("${org.quartz.scheduler.jmx.objectName}")
    private String quartzSchedulerJmxObjectName;

    @Value("${org.quartz.threadPool.class}")
    private String quartzThreadPoolClass;
    @Value("${org.quartz.threadPool.threadCount}")
    private String quartzThreadPoolThreadCount;
    @Value("${org.quartz.threadPool.threadPriority}")
    private String quartzThreadPoolThreadPriority;
    @Value("${org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread}")
    private String quartzThreadPoolInheritClassLoader;

    @Value("${org.quartz.jobStore.driverDelegateClass}")
    private String quartzJobStoreDriverDelegateClass;
    @Value("${org.quartz.jobStore.isClustered}")
    private String quartzJobStoreClustered;

    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    @Bean
    public SpringBeanJobFactory quartzSpringBeanJobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean quartzScheduler(
            JobFactory jobFactory,
            @Qualifier("countTweetsWordsJobTrigger") Trigger countTweetsWordsJobTrigger,
            @Qualifier("removeOutdatedTweetWordsJobTrigger") Trigger removeOutdatedTweetWordsJobTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);

        // this allows to update triggers in DB when updating settings in config file:
        factory.setOverwriteExistingJobs(true);

        factory.setTriggers(countTweetsWordsJobTrigger, removeOutdatedTweetWordsJobTrigger);


        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.jobStore.dataSource", "quartzDS");
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.URL", quartzDataSourceUrl);
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.user", quartzDataSourceUser);
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.password", quartzDataSourcePassword);
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.maxConnections", quartzDataSourceMaxConnections);
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.validationQuery", quartzDataSourceValidationQuery);
        quartzProperties.setProperty("org.quartz.dataSource.quartzDS.driver", quartzDataSourceDriver);

        quartzProperties.setProperty("org.quartz.scheduler.instanceName", quartzSchedulerInstanceName);
        quartzProperties.setProperty("org.quartz.scheduler.instanceId", quartzSchedulerInstanceId);
        quartzProperties.setProperty("org.quartz.scheduler.jmx.export", quartzSchedulerJmxExport);
        quartzProperties.setProperty("org.quartz.scheduler.jmx.objectName", quartzSchedulerJmxObjectName);

        quartzProperties.setProperty("org.quartz.threadPool.class", quartzThreadPoolClass);
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", quartzThreadPoolThreadCount);
        quartzProperties.setProperty("org.quartz.threadPool.threadPriority", quartzThreadPoolThreadPriority);
        quartzProperties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
                quartzThreadPoolInheritClassLoader);

        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", quartzJobStoreDriverDelegateClass);
        quartzProperties.setProperty("org.quartz.jobStore.isClustered", quartzJobStoreClustered);
        quartzProperties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        factory.setQuartzProperties(quartzProperties);

        return factory;
    }

    @Bean(name = "countTweetsWordsJobDetail")
    public JobDetailFactoryBean countTweetsWordsJobDetail() {
        return createJobDetail(CountTweetsWordsJob.class);
    }

    @Bean(name = "countTweetsWordsJobTrigger")
    public SimpleTriggerFactoryBean countTweetsWordsJobTrigger(
            @Qualifier("countTweetsWordsJobDetail") JobDetail countTweetsWordsJobDetail,
            @Value("${count.tweets.words.job.frequency}") long frequency) {
        return createTrigger(countTweetsWordsJobDetail, frequency);
    }

    @Bean(name = "removeOutdatedTweetWordsJobDetail")
    public JobDetailFactoryBean removeOutdatedTweetWordsJobDetail() {
        return createJobDetail(RemoveOutdatedTweetWordsJob.class);
    }

    @Bean(name = "removeOutdatedTweetWordsJobTrigger")
    public CronTriggerFactoryBean removeOutdatedTweetWordsJobTrigger(
            @Qualifier("removeOutdatedTweetWordsJobDetail") JobDetail removeOutdatedTweetWordsJobDetail) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(removeOutdatedTweetWordsJobDetail);
        factoryBean.setStartDelay(0L);
        // let the job run at the end of each minute
        factoryBean.setCronExpression("1 * * * * ?");
        return factoryBean;
    }
}
