package wind.instrument.competitions.rest.model.votestatistic;

import java.util.Date;

public class UserCompetition {
    private Long id;
    private String name;
    private Date start;

    public UserCompetition() {

    }

    public UserCompetition(Long id, String name, Date start) {
        this.id = id;
        this.name = name;
        this.start = start;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}
