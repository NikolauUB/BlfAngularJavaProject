package wind.instrument.competitions.data;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "ThemeEntity")
@Table(name = "theme", schema = "forumdata")
public class ThemeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "theme_id")
    private Long id;
    @Column(name = "competition_id")
    private Long competitionId;
    @Column(name = "theme_name", columnDefinition = "TEXT")
    private String name;
    @Column(name = "theme_type", nullable = false)
    private Integer themeType;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity owner;
    @OneToMany(mappedBy = "theme", targetEntity = MessageEntity.class, fetch = FetchType.LAZY)
    private Collection<MessageEntity> messages;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", referencedColumnName = "competition_id", insertable = false, updatable = false)
    private CompetitionEntity competition;
    @OneToMany(mappedBy = "theme", targetEntity = CompetitionThemeEntity.class, fetch = FetchType.LAZY)
    private Collection<CompetitionThemeEntity> competitionTheme;

    private Date created;
    private Date updated;


    @PrePersist
    protected void onCreate() {
        this.created = new Date();
        this.updated = this.getCreated();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThemeType getThemeType() {
        return ThemeType.valueOf(this.themeType);
    }

    public void setThemeType(ThemeType themeType) {
        this.themeType = themeType.getValue();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Collection<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Collection<MessageEntity> messages) {
        this.messages = messages;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Date getUpdated() {
        return updated;
    }

    public Date setUpdated(Date updated) {
        return this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }
}
