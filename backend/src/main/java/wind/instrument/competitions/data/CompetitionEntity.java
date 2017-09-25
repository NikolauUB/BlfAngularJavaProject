package wind.instrument.competitions.data;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity(name="CompetitionEntity")
@Table(name = "competitions", schema = "forumdata")
public class CompetitionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name =  "competition_id")
    private Long competitionId;
    @Column(name =  "competition_name")
    private String competitionName;
    @Column(name =  "competition_desc", columnDefinition = "TEXT")
    private String competitionDesc;
    @Column(name =  "competition_sample_video")
    private String competitionSampleVideo;
    @Column(name =  "competition_start")
    private Date competitionStart;
    @Column(name =  "competition_end")
    private Date competitionEnd;
    @Column(name =  "competition_type", nullable = false)
    private Integer competitionType;
    @Column(name =  "active", nullable = false)
    private Boolean active;


    @OneToMany(mappedBy = "competition", targetEntity=CompetitionItemEntity.class, fetch = FetchType.LAZY)
    private Collection<CompetitionItemEntity> competitionItems;

    @OneToMany(mappedBy = "competition", targetEntity=ThemeEntity.class, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<ThemeEntity> themesByMembers;


    private Date created;
    private Date updated;

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
        this.updated = this.created;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }


    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Date getCompetitionStart() {
        return competitionStart;
    }

    public void setCompetitionStart(Date competitionStart) {
        this.competitionStart = competitionStart;
    }

    public Date getCompetitionEnd() {
        return competitionEnd;
    }

    public void setCompetitionEnd(Date competitionEnd) {
        this.competitionEnd = competitionEnd;
    }

    public CompetitionType getCompetitionType() {
        return CompetitionType.valueOf(this.competitionType);
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType.getValue();
    }

    public Collection<CompetitionItemEntity> getCompetitionItems() {
        return competitionItems;
    }

    public void setCompetitionItems(Collection<CompetitionItemEntity> competitionItems) {
        this.competitionItems = competitionItems;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {

        this.active = active;
    }

    public String toDebugString() {
        return this.getCompetitionName() + ", " +
                this.getCompetitionDesc() + ", " +
                this.getCompetitionStart() + ", " +
                this.getCompetitionEnd() + ", " +
                this.getActive();
    }

    public String getCompetitionDesc() {
        return competitionDesc;
    }

    public void setCompetitionDesc(String competitionDesc) {
        this.competitionDesc = competitionDesc;
    }

    public String getCompetitionSampleVideo() {
        return competitionSampleVideo;
    }

    public void setCompetitionSampleVideo(String competitionSampleVideo) {
        this.competitionSampleVideo = competitionSampleVideo;
    }

    public Collection<ThemeEntity> getThemesByMembers() {
        return themesByMembers;
    }

    public Date getUpdated() {
        return updated;
    }
}
