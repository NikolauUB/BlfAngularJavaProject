package wind.instrument.competitions.rest.model.votestatistic;

import java.util.List;

public class UserStatisticHistory {
    private Long userId;
    private Integer leaves;
    private Integer broomType;
    private List<UserCompetition> compIds;

    public UserStatisticHistory() {}

    public UserStatisticHistory(Long userId,
                                Integer leaves,
                                Integer broomType,
                                List<UserCompetition> compIds) {

        this.userId = userId;
        this.leaves = leaves;
        this.broomType = broomType;
        this.compIds = compIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLeaves() {
        return leaves;
    }

    public void setLeaves(Integer leaves) {
        this.leaves = leaves;
    }

    public Integer getBroomType() {
        return broomType;
    }

    public void setBroomType(Integer broomType) {
        this.broomType = broomType;
    }

    public List<UserCompetition> getCompIds() {
        return compIds;
    }

    public void setCompIds(List<UserCompetition> compIds) {
        this.compIds = compIds;
    }
}
