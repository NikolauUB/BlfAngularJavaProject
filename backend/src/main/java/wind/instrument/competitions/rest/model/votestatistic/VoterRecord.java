package wind.instrument.competitions.rest.model.votestatistic;

import java.util.HashMap;

public class VoterRecord {
    private Long voterId;
    private HashMap<Long, Integer> voterRawMap = new HashMap<Long, Integer>();
    private HashMap<Long, Integer> voterPlaceMap = new HashMap<Long, Integer>();

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public HashMap<Long, Integer> getVoterRawMap() {
        return voterRawMap;
    }

    public void setVoterRawMap(HashMap<Long, Integer> voterRawMap) {
        this.voterRawMap = voterRawMap;
    }


    public HashMap<Long, Integer> getVoterPlaceMap() {
        return voterPlaceMap;
    }

    public void setVoterPlaceMap(HashMap<Long, Integer> voterPlaceMap) {
        this.voterPlaceMap = voterPlaceMap;
    }
}
