package wind.instrument.competitions.rest.model;

import java.util.Date;

public class CompetitionData {
    private long id;
    private String name;
    private int type;
    private String description;
    private Date start;
    private Date end;
    public CompetitionData() {}
    public CompetitionData(long id,
                           String name,
                           int type,
                           String description,
                           Date start,
                           Date end) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
