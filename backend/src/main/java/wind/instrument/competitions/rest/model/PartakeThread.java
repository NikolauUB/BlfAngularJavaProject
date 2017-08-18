package wind.instrument.competitions.rest.model;

import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import java.util.ArrayList;

public class PartakeThread {
    private int code;
    private String errorMsg;
    private ArrayList<DiscussionItem> discussionItems;

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

    public ArrayList<DiscussionItem> getDiscussionItems() {
        return discussionItems;
    }

    public void setDiscussionItems(ArrayList<DiscussionItem> discussionItems) {
        this.discussionItems = discussionItems;
    }
}
