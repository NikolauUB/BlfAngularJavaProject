package wind.instrument.competitions.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "CompetitionVotingEntity")
@Table(name = "competition_voting", schema = "forumdata", uniqueConstraints =
@UniqueConstraint(columnNames = {"user_id", "competition_item_id"}))
public class CompetitionVotingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "competition_voting_id")
    private Long competitionVotingId;
    @Column(name = "competition_id")
    private Long competitionId;
    @Column(name = "competition_item_id")
    private Long competitionItemId;
    @Column(name = "voting_order")
    private Integer votingOrder;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", referencedColumnName = "competition_id", insertable = false, updatable = false)
    private CompetitionEntity competition;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_item_id", referencedColumnName = "competition_item_id", insertable = false, updatable = false)
    private CompetitionItemEntity competitionItem;


    private Date created;
    private Date updated;

    @PrePersist
    protected void onCreate() {
        this.setCreated(new Date());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    public Long getCompetitionVotingId() {
        return competitionVotingId;
    }

    public void setCompetitionVotingId(Long competitionVotingId) {
        this.competitionVotingId = competitionVotingId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getCompetitionItemId() {
        return competitionItemId;
    }

    public void setCompetitionItemId(Long competitionItemId) {
        this.competitionItemId = competitionItemId;
    }

    public Integer getVotingOrder() {
        return votingOrder;
    }

    public void setVotingOrder(Integer votingOrder) {
        this.votingOrder = votingOrder;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public CompetitionItemEntity getCompetitionItem() {
        return competitionItem;
    }

    public void setCompetitionItem(CompetitionItemEntity competitionItem) {
        this.competitionItem = competitionItem;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
