package wind.instrument.competitions.rest.model;

import java.util.Date;

public class UserData {
    private String username;
    private String saveImage;
    private String previewImage;
    private Date updated;
    private Date created;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSaveImage() {
        return saveImage;
    }

    public void setSaveImage(String saveImage) {
        this.saveImage = saveImage;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
