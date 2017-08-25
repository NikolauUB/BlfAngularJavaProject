package wind.instrument.competitions.rest.model.changes;

import java.util.ArrayList;

public class ChangesKeywords {
    private Long time;
    private ArrayList<String> keywords;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
