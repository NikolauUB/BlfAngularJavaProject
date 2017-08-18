package wind.instrument.competitions.data;

import wind.instrument.competitions.middle.Utils;

import javax.persistence.*;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Entity(name = "UserEntity")
@Table(name = "users", schema = "forumdata")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "password_salt")
    private String passwordSalt;
    @Transient
    private String typedPassword;

    @Lob
    @Column(name = "image", length = 100000)
    private byte[] image;

    private Date created;
    private Date updated;
    private Date lastVisit;

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long id) {
        this.userId = userId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTypedPassword() {
        return typedPassword;
    }

    public void setTypedPassword(String typedPassword) {
        this.typedPassword = typedPassword;
    }


    public void generatePasswordHashes() throws NoSuchAlgorithmException {
        this.passwordSalt = Utils.generateNewSalt();
        this.passwordHash = Utils.encryptPassword(this.getTypedPassword(), this.passwordSalt);
    }

    public Date getCreated() {
        return created;
    }

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }
}
