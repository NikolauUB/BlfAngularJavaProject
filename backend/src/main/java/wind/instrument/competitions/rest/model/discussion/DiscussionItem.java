package wind.instrument.competitions.rest.model.discussion;

import java.util.Date;

public class DiscussionItem {
    private Long competitionId;
    private Long msgThreadId;
    private Long msgId;
    private Long parentMsgId;
    private Long authorId;
    private String msgText;
    private Date creationDate;
    private Date updateDate;

    public Long getMsgThreadId() {
        return msgThreadId;
    }

    public void setMsgThreadId(Long msgThreadId) {
        this.msgThreadId = msgThreadId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(Long parentMsgId) {
        this.parentMsgId = parentMsgId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
