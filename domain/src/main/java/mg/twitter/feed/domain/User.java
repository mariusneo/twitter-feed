package mg.twitter.feed.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
public class User extends AbstractPersistable<Long> {
    private String name;

    private String screeName;

    private String description;

    private byte[] image;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreeName() {
        return screeName;
    }

    public void setScreeName(String screeName) {
        this.screeName = screeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                "screeName='" + screeName + '\'' +
                '}';
    }
}
