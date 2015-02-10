package mg.twitter.feed.jobs.countwords.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountWordsTweetRepository extends JpaRepository<CountWordsTweet, Long>,
        CountWordsTweetRepositoryCustom {
}
