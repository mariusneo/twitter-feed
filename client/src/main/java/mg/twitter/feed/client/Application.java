package mg.twitter.feed.client;

import mg.twitter.feed.client.tweets.TweetsResourceClient;
import mg.twitter.feed.contract.Tweet;

public class Application {
    public static void  main(String[] args){
        TweetsResourceClient tweetsResourceClient = new TweetsResourceClient("http://localhost:9080/api");
        Tweet tweet = tweetsResourceClient.readTweet(724l);
        System.out.println(tweet.getText());
    }
}
