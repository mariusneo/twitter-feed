package mg.twitter.feed.web.controller;

import mg.twitter.feed.client.tweets.TweetsResourceClient;
import mg.twitter.feed.contract.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedController {
    private static final int TWEETS_PER_PAGE = 20;


    @Autowired
    TweetsResourceClient tweetsResourceClient;

    @RequestMapping("/")
    public String feed(Model model) {
        Tweet[] tweets = tweetsResourceClient.findLatestTweets(20);
        model.addAttribute("tweets", tweets);
        return "feed";
    }
}