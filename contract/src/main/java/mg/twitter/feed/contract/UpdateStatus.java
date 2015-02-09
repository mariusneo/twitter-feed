package mg.twitter.feed.contract;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateStatus {
    private boolean available;
    private Long sinceTweetId;

    public UpdateStatus(){

    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getSinceTweetId() {
        return sinceTweetId;
    }

    public void setSinceTweetId(Long sinceTweetId) {
        this.sinceTweetId = sinceTweetId;
    }
}
