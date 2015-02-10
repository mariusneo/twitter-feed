package mg.twitter.feed.jobs.countwords.domain;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CountWordsTweetRepositoryCustom {
    @Transactional(readOnly = true)
    List<CountWordsTweet> findNewCountWordsTweets(int limit);

    @Transactional
    int updateStatus(Long tweetId, CountWordsTweetStatus status);

    @Transactional
    int updateStatus(List<Long> tweetIds, CountWordsTweetStatus status);

}
