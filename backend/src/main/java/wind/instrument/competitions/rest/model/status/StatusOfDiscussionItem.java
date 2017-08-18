package wind.instrument.competitions.rest.model.status;

import wind.instrument.competitions.rest.model.discussion.DiscussionItem;


public class StatusOfDiscussionItem {
    private int code;
    private String errorMsg;
    private DiscussionItem item;

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


    public DiscussionItem getItem() {
        return item;
    }

    public void setItem(DiscussionItem item) {
        this.item = item;
    }
}
