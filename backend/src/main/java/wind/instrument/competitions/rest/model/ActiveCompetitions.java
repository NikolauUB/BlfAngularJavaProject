package wind.instrument.competitions.rest.model;

import java.util.ArrayList;

public class ActiveCompetitions {
    private int code;
    private String errorMsg;
    private ArrayList<CompetitionData> activeList;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<CompetitionData> getActiveList() {
        return activeList;
    }

    public void setActiveList(ArrayList<CompetitionData> activeList) {
        this.activeList = activeList;
    }
}
