package mg.twitter.feed.importer;


import mg.twitter.feed.importer.config.JdbConfig;
import mg.twitter.feed.importer.config.TweetAsyncConfig;
import mg.twitter.feed.importer.service.TweetImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;


@ComponentScan
@EnableAutoConfiguration
@EnableAsync
@EnableJpaRepositories(value = "mg.twitter.feed.domain")
@Import({JdbConfig.class, TweetAsyncConfig.class})
public class Application implements CommandLineRunner{
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static final String TWITTER_AUTH_FILE_PROPERTY = "twitter.auth.file";

    @Autowired
    private TweetImporterService tweetImporterService;

    @Override
    public void run(String... args) throws Exception {
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                tweetImporterService.importTweet(status);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            public void onException(Exception ex) {
                LOGGER.error("Exception occured in the Twitter stream listener", ex);
            }
        };

        Properties twitterAuthorization = new Properties();
        String propertyFileName = System.getProperty(TWITTER_AUTH_FILE_PROPERTY);
        if (StringUtils.isEmpty(propertyFileName)) {
            throw new IllegalArgumentException("The property " + TWITTER_AUTH_FILE_PROPERTY + " is not specified");
        }
        InputStream in = new FileInputStream(propertyFileName);
        if (in == null) {
            throw new FileNotFoundException("The file " + propertyFileName + " specified for the property " +
                    TWITTER_AUTH_FILE_PROPERTY + " system property does not exist");
        }
        twitterAuthorization.load(in);

        String consumerKey = twitterAuthorization.getProperty("consumer_key");
        String consumerSecret = twitterAuthorization.getProperty("consumer_secret");
        String accessToken = twitterAuthorization.getProperty("access_token");
        String accessTokenSecret = twitterAuthorization.getProperty("access_token_secret");

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
        twitterStream.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        twitterStream.addListener(listener);

        FilterQuery germanCapitalsQuery = new FilterQuery();
        germanCapitalsQuery.track(new String[]{"Wien", "wien", "Berlin", "berlin", "Bern", "bern", "Vaduz", "vaduz"});
        //germanCapitalsQuery.language(new String[]{"de"});
        twitterStream.filter(germanCapitalsQuery);

        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate
        // listener methods continuously.
        //twitterStream.sample();

//        sapi.filter(track=['Java', 'python', 'C'])
//        sapi.filter(track=['tennis', 'football', 'basket'])
//        sapi.filter(track=['USD', 'EUR', 'CHF'])
//         Vienna
//         coordinates W,S E,N
//        sapi.filter(locations=[16.19, 48.08, 16.62, 48.32])
//         San Francisco
//         coordinates WS EN
//        sapi.filter(locations=[-122.75,36.8,-121.75,37.8])
//         Europe
//        sapi.filter(locations=[-24.01, 36.29, 41.32, 70.62])
    }


    public static void main(String[] args){
        LOGGER.info("Starting the import of tweets");
        SpringApplication.run(Application.class, args);
    }
}
