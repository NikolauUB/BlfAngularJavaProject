package wind.instrument.competitions.data;

import javax.persistence.*;
import java.util.Date;
@Entity(name="MessageEntity")
@Table(name = "message", schema = "forumdata")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name =  "msg_id")
    private Long msgId;
    @Column(name =  "theme_id")
    private Long themeId;
    @Column(name = "is_hidden")
    private Boolean isHidden;
    @Column(name = "reason_to_hide")
    private Integer reasonToHidden;
    @Column(name =  "msg_body", nullable = false, columnDefinition = "TEXT")
    private String msgBody;
    @Column(name =  "msg_positive_rate")
    private Integer msgPositiveRate;
    @Column(name =  "msg_negative_rate")
    private Integer msgNegativeRate;
    @Column(name =  "parent_msg_id")
    private Long parentMsgId;
    @Column(name = "user_id_replied_to")
    private Long userIdRepliedTo;
    @Column(name =  "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity owner;
    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id", referencedColumnName = "theme_id", insertable = false, updatable = false)
    private ThemeEntity theme;


    private Date created;
    private Date updated;

    @PrePersist
    protected void onCreate() {
        this.setCreated(new Date());
        this.setUpdated(this.getCreated());
    }

    @PreUpdate
    protected void onUpdate() {
        this.setUpdated(new Date());
    }


    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public Integer getMsgPositiveRate() {
        return msgPositiveRate;
    }

    public void setMsgPositiveRate(Integer messagePositiveRate) {
        this.msgPositiveRate = messagePositiveRate;
    }

    public Integer getMessageNegativeRate() {
        return msgNegativeRate;
    }

    public void setMessageNegativeRate(Integer messageNegativeRate) {
        this.msgNegativeRate = messageNegativeRate;
    }

    public Long getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(Long parentMsgId) {
        this.parentMsgId = parentMsgId;
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

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public Long getUserIdRepliedTo() {
        return userIdRepliedTo;
    }

    public void setUserIdRepliedTo(Long userIdRepliedTo) {
        this.userIdRepliedTo = userIdRepliedTo;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public HiddenMessageType getReasonToHidden() {
        return HiddenMessageType.valueOf(this.reasonToHidden);
    }

    public void setReasonToHidden(HiddenMessageType reasonToHidden) {
        this.reasonToHidden = reasonToHidden.getValue();
    }

    public ThemeEntity getTheme() {
        return theme;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
