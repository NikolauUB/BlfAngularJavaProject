package wind.instrument.competitions.rest.model.changes;

import java.util.ArrayList;

public class UsersChanges {
    private ArrayList<Long> userIds;

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }
}
