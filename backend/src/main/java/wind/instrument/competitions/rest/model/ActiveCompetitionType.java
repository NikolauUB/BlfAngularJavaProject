package wind.instrument.competitions.rest.model;

import java.util.Date;

public class ActiveCompetitionType {
    private Integer type;
    private Date start;
    private Date end;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
