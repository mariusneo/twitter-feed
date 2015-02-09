package mg.twitter.feed.web.config;

import mg.twitter.feed.client.tweets.TweetsResourceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Value("${twitter.feed.service.url}")
    private String twitterFeedServiceUrl;

    @Bean
    public TweetsResourceClient tweetsResourceClient(){
        return new TweetsResourceClient(twitterFeedServiceUrl);
    }


    @Bean(name = "urlService")
    public UrlService urlService() {
        return new UrlService() {
            @Override
            public String getServiceApplicationUrl() {
                return twitterFeedServiceUrl;
            }
        };
    }

}


