package wind.instrument.competitions.data;

import javax.persistence.*;

@Entity(name = "CompetitionVotingSummary")
@Table(name = "competition_voting_summary", schema = "forumdata")
public class CompetitionVotingSummary {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "leaf_summary")
    private Integer leafSummary;
    @Column(name = "broomType")
    private Integer broomType = 0;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLeafSummary() {
        return leafSummary;
    }

    public void setLeafSummary(Integer leafSummary) {
        this.leafSummary = leafSummary;
    }

    public BroomType getBroomType() {
        return BroomType.valueOf(this.broomType);
    }

    public void setBroomType(BroomType broomType) {
        this.broomType = broomType.getValue();
    }

}

