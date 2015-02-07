package mg.twitter.feed.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "tweets")
public class Tweet extends AbstractPersistable<Long> {

    private String text;

    @NotNull
    private Long userId;

    @NotNull
    private String userName;

    @Column(name = "createdAt")
    private Date createdAt;

    public Tweet() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + getId() +
                ", userName='" + userName +
                "', text='" + text + '\'' +
                '}';
    }
}
