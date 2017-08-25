package wind.instrument.competitions.rest.model;

import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import java.util.ArrayList;

public class PartakeThread {
    private ArrayList<DiscussionItem> discussionItems;

    public ArrayList<DiscussionItem> getDiscussionItems() {
        return discussionItems;
    }

    public void setDiscussionItems(ArrayList<DiscussionItem> discussionItems) {
        this.discussionItems = discussionItems;
    }
}
