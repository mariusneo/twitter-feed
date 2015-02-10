package mg.twitter.feed.jobs.countwords.service;

import mg.twitter.feed.domain.Tweet;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweet;
import mg.twitter.feed.jobs.countwords.domain.CountWordsTweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountTweetWordsService {
    @Autowired
    private CountWordsTweetRepository countWordsTweetRepository;

    @Autowired
    private TweetRepository tweetRepository;


    @Async
    @Transactional
    public void processCountWordsTweet(CountWordsTweet countWordsTweet) {
        Long tweetId = countWordsTweet.getTweetId();

        Tweet tweet = tweetRepository.findOne(tweetId);

        // processing
        System.out.println(tweet.getText());

        // in OK case, delete the entry
        countWordsTweetRepository.delete(tweetId);
    }
}
