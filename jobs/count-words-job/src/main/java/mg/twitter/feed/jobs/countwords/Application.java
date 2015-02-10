package mg.twitter.feed.jobs.countwords;

import mg.twitter.feed.jobs.countwords.config.CountTweetWordsAsyncConfig;
import mg.twitter.feed.jobs.countwords.config.JdbConfig;
import mg.twitter.feed.jobs.countwords.config.QuartzConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories(value = {"mg.twitter.feed.domain", "mg.twitter.feed.jobs.countwords.domain"})
@Import({JdbConfig.class, QuartzConfig.class, CountTweetWordsAsyncConfig.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
