package wind.instrument.competitions.rest.model;

import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import java.util.ArrayList;

public class VotingThread {
    private ArrayList<DiscussionItem> oi;
    private Long id;
    //Id of younger item of the page. Previous page will start after it
    private Long yid;
    //Count of younger items
    private Long yc = new Long(-1);
    //Count of all items
    private Long ac = new Long(0);
    //Was changes from cd or not
    private boolean c = true;

    public ArrayList<DiscussionItem> getOi() {
        return oi;
    }

    public void setOi(ArrayList<DiscussionItem> oi) {
        this.oi = oi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Id of younger item of the page. Previous page will start after it
     */
    public Long getYid() {
        return yid;
    }

    public void setYid(Long yid) {
        this.yid = yid;
    }

    /**
     * Count of younger items
     */
    public Long getYc() {
        return yc;
    }

    public void setYc(Long yc) {
        this.yc = yc;
    }

    /**
     * Was changes from cd or not
     */
    public boolean isC() {
        return c;
    }

    public void setC(boolean c) {
        this.c = c;
    }

    /**
     * Count of all items
     */
    public Long getAc() {
        return ac;
    }

    public void setAc(Long ac) {
        this.ac = ac;
    }
}
