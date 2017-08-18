package wind.instrument.competitions.rest.model.changedobjects;

import java.util.ArrayList;

public class PartakeThreadChanges {
    private int code;
    private String errorMsg;
    private ArrayList<Long> deletedIds;
    private ArrayList<Long> changedIds;

    public PartakeThreadChanges() {
        this.changedIds = new ArrayList<Long>();
        this.deletedIds = new ArrayList<Long>();
    }

    public ArrayList<Long> getDeletedIds() {
        return deletedIds;
    }

    public void setDeletedIds(ArrayList<Long> deletedIds) {
        this.deletedIds = deletedIds;
    }

    public ArrayList<Long> getChangedIds() {
        return changedIds;
    }

    public void setChangedIds(ArrayList<Long> changedIds) {
        this.changedIds = changedIds;
    }

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
}
