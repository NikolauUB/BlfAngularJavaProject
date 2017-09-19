package wind.instrument.competitions.rest.model;


import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class CompetitionInfo {
    private CompetitionData competitionData;
    private boolean voted = false;
    private Collection<VoteData> voteData;

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

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}
