package wind.instrument.competitions.rest.model.votestatistic;

import java.util.ArrayList;
import java.util.List;

public class VoteStatistic {
    private List<Long> allVoteItemIdList = new ArrayList<Long>();
    private List<VoterRecord> voters = new  ArrayList<VoterRecord>();

    public List<Long> getAllVoteItemIdList() {
        return allVoteItemIdList;
    }

    public void setAllVoteItemIdList(List<Long> allVoteItemIdList) {
        this.allVoteItemIdList = allVoteItemIdList;
    }

    public List<VoterRecord> getVoters() {
        return voters;
    }

    public void setVoters(List<VoterRecord> voters) {
        this.voters = voters;
    }
}
