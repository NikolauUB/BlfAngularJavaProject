package wind.instrument.competitions.rest.model.changes;

import java.util.ArrayList;

public class ThreadChanges {
    private ArrayList<Long> userIds;
    private ArrayList<Long> msgIds;
    private Boolean thChanged;

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }

    public ArrayList<Long> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(ArrayList<Long> msgIds) {
        this.msgIds = msgIds;
    }

    public Boolean getThChanged() {
        return thChanged;
    }

    public void setThChanged(Boolean thChanged) {
        this.thChanged = thChanged;
    }
}
