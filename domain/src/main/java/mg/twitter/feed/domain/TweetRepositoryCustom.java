package mg.twitter.feed.domain;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TweetRepositoryCustom {

    @Transactional(readOnly = true)
    List<Tweet> findPreviousTweets(long maxTweetId, int count);

    @Transactional(readOnly = true)
    List<Tweet> findLatestTweets(long sinceTweetId, int count);

    @Transactional(readOnly = true)
    List<Tweet> findLatestTweets(int count);

    @Transactional(readOnly = true)
    boolean updatesAvaiable(long sinceTweetId);
}
