package wind.instrument.competitions.rest.model;


import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class CompetitionInfo {
    private int code = HttpServletResponse.SC_OK;
    private String errorMsg;
    private CompetitionData competitionData;
    private Collection<VoteData> voteData;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public CompetitionData getCompetitionData() {
        return competitionData;
    }

    public void setCompetitionData(CompetitionData competitionData) {
        this.competitionData = competitionData;
    }

    public Collection<VoteData> getVoteData() {
        return voteData;
    }

    public void setVoteData(Collection<VoteData> voteData) {
        this.voteData = voteData;
    }
}
