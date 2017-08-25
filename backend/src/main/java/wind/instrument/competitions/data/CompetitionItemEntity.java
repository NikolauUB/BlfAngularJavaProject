package wind.instrument.competitions.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="CompetitionItemEntity")
@Table(name = "competition_items", schema = "forumdata")
public class CompetitionItemEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name =  "competition_item_id")
    private Long competitionItemId;
    @Column(name =  "competition_id")
    private Long competitionId;
    @Column(name =  "cn_item_composition")
    private String cnItemComposition;
    @Column(name =  "cn_item_author")
    private String cnItemAuthor;
    @Column(name =  "cn_item_instruments")
    private String cnItemInstruments;
    @Column(name =  "cn_item_description", columnDefinition = "TEXT")
    private String cnItemDescription;
    @Column(name =  "cn_item_audio")
    private String cnItemAudio;
    @Column(name =  "cn_item_video")
    private String cnItemVideo;
    @Column(name =  "user_id")
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competition_id", referencedColumnName = "competition_id", insertable = false, updatable = false)
    private CompetitionEntity competition;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity owner;

    private Date created;
    private Date updated;

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    public Long getCompetitionItemId() {
        return competitionItemId;
    }

    public void setCompetitionItemId(Long competitionItemId) {
        this.competitionItemId = competitionItemId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCnItemComposition() {
        return cnItemComposition;
    }

    public void setCnItemComposition(String cnItemComposition) {
        this.cnItemComposition = cnItemComposition;
    }

    public String getCnItemAuthor() {
        return cnItemAuthor;
    }

    public void setCnItemAuthor(String cnItemAuthor) {
        this.cnItemAuthor = cnItemAuthor;
    }

    public String getCnItemInstruments() {
        return cnItemInstruments;
    }

    public void setCnItemInstruments(String cnItemInstruments) {
        this.cnItemInstruments = cnItemInstruments;
    }

    public String getCnItemDescription() {
        return cnItemDescription;
    }

    public void setCnItemDescription(String cnItemDescription) {
        this.cnItemDescription = cnItemDescription;
    }

    public String getCnItemAudio() {
        return cnItemAudio;
    }

    public void setCnItemAudio(String cnItemAudio) {
        this.cnItemAudio = cnItemAudio;
    }

    public String getCnItemVideo() {
        return cnItemVideo;
    }

    public void setCnItemVideo(String cnItemVideo) {
        this.cnItemVideo = cnItemVideo;
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
}
