package mg.twitter.feed.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class TweetRepositoryImpl implements TweetRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Tweet> findLatestTweets(long sinceTweetId, int count) {
        String hql = "SELECT t from Tweet t ";
        if (sinceTweetId != 0)
            hql += "WHERE t.id > :sinceTweetId ";
        hql += "ORDER BY t.createdAt DESC";
        TypedQuery<Tweet> query = em.createQuery(hql, Tweet.class);
        if (sinceTweetId != 0)
            query.setParameter("sinceTweetId", sinceTweetId);

        return query.setMaxResults(count).getResultList();
    }

    @Override
    public List<Tweet> findLatestTweets(int count) {
        return findLatestTweets(0, count);
    }
}
