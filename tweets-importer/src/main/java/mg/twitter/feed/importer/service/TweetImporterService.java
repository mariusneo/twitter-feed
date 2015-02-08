package mg.twitter.feed.importer.service;

import mg.twitter.feed.domain.Tweet;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.domain.User;
import mg.twitter.feed.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Status;

@Service
public class TweetImporterService {

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    @Async
    @Transactional
    public void importTweet(Status status){
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(status.getCreatedAt());
        tweet.setText(status.getText());
        tweet.setUserName(status.getUser().getName());
        long userId = status.getUser().getId();
        tweet.setUserId(userId);
        tweetRepository.save(tweet);

        User user = userRepository.findOne(userId);
        if (user == null){
            user = new User();
            user.setId(status.getUser().getId());
            user.setName(status.getUser().getName());
            user.setScreenName(status.getUser().getScreenName());
            user.setProfileImageUrl(status.getUser().getProfileImageURL());
            user.setDescription(status.getUser().getDescription());
            userRepository.save(user);
        }
    }
}
