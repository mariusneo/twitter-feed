package mg.twitter.feed.jobs.countwords.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "count_words_tweets")
public class CountWordsTweet implements Persistable<Long> {
    @Id
    @Column(name = "tweet_id")
    private Long id;

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

    public Long getId() {

        return id;
    }

    public void setId(final Long id) {

        this.id = id;
    }

    public boolean isNew() {

        return null == getId();
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        Persistable<?> that = (Persistable<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
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
