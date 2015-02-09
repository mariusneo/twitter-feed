package mg.twitter.feed.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class TweetRepositoryImpl implements TweetRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Tweet> findPreviousTweets(long maxTweetId, int count){
        String hql = "SELECT t from Tweet t ";
        if (maxTweetId != 0)
            hql += "WHERE t.id <= :maxTweetId ";
        hql += "ORDER BY t.createdAt DESC, t.id DESC";
        TypedQuery<Tweet> query = em.createQuery(hql, Tweet.class);
        if (maxTweetId != 0)
            query.setParameter("maxTweetId", maxTweetId);

        return query.setMaxResults(count).getResultList();
    }


    @Override
    public List<Tweet> findLatestTweets(long sinceTweetId, int count) {
        String hql = "SELECT t from Tweet t ";
        if (sinceTweetId != 0)
            hql += "WHERE t.id > :sinceTweetId ";
        hql += "ORDER BY t.createdAt DESC, t.id DESC";
        TypedQuery<Tweet> query = em.createQuery(hql, Tweet.class);
        if (sinceTweetId != 0)
            query.setParameter("sinceTweetId", sinceTweetId);

        return query.setMaxResults(count).getResultList();
    }

    @Override
    public List<Tweet> findLatestTweets(int count) {
        return findLatestTweets(0, count);
    }

    public boolean updatesAvaiable(long sinceTweetId){
        String hql = "SELECT 1 from Tweet t "+
            "WHERE t.id > :sinceTweetId" ;
        TypedQuery<Integer> query = em.createQuery(hql,Integer.class);
        query.setParameter("sinceTweetId", sinceTweetId);

        return !query.setMaxResults(1).getResultList().isEmpty();
    }
}
