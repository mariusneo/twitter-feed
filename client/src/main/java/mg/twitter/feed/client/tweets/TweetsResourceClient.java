package mg.twitter.feed.client.tweets;

import mg.twitter.feed.contract.Tweet;
import org.springframework.web.client.RestTemplate;

/**
 * Created by marius on 08.02.15.
 */
public class TweetsResourceClient {
    private String serviceUrl;

    private RestTemplate restTemplate;

    public TweetsResourceClient(String serviceUrl){
        this.serviceUrl = serviceUrl + "/tweets";
        this.restTemplate = new RestTemplate();
    }

    public Tweet readTweet(Long id){
        return restTemplate.getForObject(serviceUrl+"/{id}", Tweet.class, id.toString());
    }

}
