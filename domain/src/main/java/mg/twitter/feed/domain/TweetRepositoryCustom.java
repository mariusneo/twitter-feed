package mg.twitter.feed.domain;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TweetRepositoryCustom {

    @Transactional(readOnly = true)
    public List<Tweet> findLatestTweets(long sinceTweetId, int count);

    @Transactional(readOnly = true)
    public List<Tweet> findLatestTweets(int count);
}
