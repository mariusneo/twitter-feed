package mg.twitter.feed.jobs.countwords.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class CountWordsTweetRepositoryImpl implements CountWordsTweetRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    public List<CountWordsTweet> findNewCountWordsTweets(int limit) {
        String hql = "SELECT t from CountWordsTweet t ";
        hql += "WHERE t.status = :availableStatus ";
        TypedQuery<CountWordsTweet> query = em.createQuery(hql, CountWordsTweet.class);
        query.setParameter("availableStatus", CountWordsTweetStatus.INITIAL);

        return query.setMaxResults(limit).getResultList();
    }

    @Override
    public int updateStatus(Long tweetId, CountWordsTweetStatus status) {
        String hql = "UPDATE CountWordsTweet SET status = :status, updatedAt = :updatedAt ";
        hql += "WHERE id = :tweetId";

        Query query = em.createQuery(hql);
        query.setParameter("status", status);
        query.setParameter("updatedAt", new Date());
        query.setParameter("tweetId", tweetId);

        return query.executeUpdate();
    }

    @Override
    public int updateStatus(List<Long> tweetIds, CountWordsTweetStatus status) {
        String hql = "UPDATE CountWordsTweet SET status = :status, updatedAt = :updatedAt ";
        hql += "WHERE id IN :tweetIds";

        Query query = em.createQuery(hql);
        query.setParameter("status", status);
        query.setParameter("updatedAt", new Date());
        query.setParameter("tweetIds", tweetIds);

        return query.executeUpdate();
    }
}
