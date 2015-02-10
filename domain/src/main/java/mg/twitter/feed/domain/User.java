package mg.twitter.feed.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User implements Persistable<Long> {
    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String screenName;

    private String description;

    private String profileImageUrl;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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


    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                "screenName='" + screenName + '\'' +
                '}';
    }
}
