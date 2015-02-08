package mg.twitter.feed.client;

import mg.twitter.feed.client.tweets.TweetsResourceClient;
import mg.twitter.feed.contract.Tweet;

import java.util.Arrays;

class Application {
    public static void  main(String[] args){
        TweetsResourceClient tweetsResourceClient = new TweetsResourceClient("http://localhost:9080/api");
        Tweet tweet = tweetsResourceClient.readTweet(724l);
        System.out.println(tweet.getText());

        Tweet[] tweets = tweetsResourceClient.findLatestTweets(10);
        Arrays.stream(tweets).forEach(t -> System.out.println(t.getText()));
    }
}
