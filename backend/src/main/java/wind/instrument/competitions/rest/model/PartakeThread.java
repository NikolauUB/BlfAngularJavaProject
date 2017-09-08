package wind.instrument.competitions.rest.model;

import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import java.util.ArrayList;
import java.util.Date;

public class PartakeThread {
    private ArrayList<DiscussionItem> discussionItems;

    private Long threadId;

    private Date thUpdated;


    public ArrayList<DiscussionItem> getDiscussionItems() {
        return discussionItems;
    }

    public void setDiscussionItems(ArrayList<DiscussionItem> discussionItems) {
        this.discussionItems = discussionItems;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Date getThUpdated() {
        return thUpdated;
    }

    public void setThUpdated(Date thUpdated) {
        this.thUpdated = thUpdated;
    }
}
