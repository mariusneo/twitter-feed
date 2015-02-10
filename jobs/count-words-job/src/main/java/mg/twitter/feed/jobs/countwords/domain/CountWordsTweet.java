package mg.twitter.feed.jobs.countwords.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class CountWordsTweet {
    @Id
    private Long tweetId;

    @NotNull
    private Date createdAt;

    @NotNull
    private Date updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CountWordsTweetStatus status;

    public CountWordsTweet() {
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CountWordsTweetStatus getStatus() {
        return status;
    }

    public void setStatus(CountWordsTweetStatus status) {
        this.status = status;
    }
}
