package wind.instrument.competitions.rest.model;

import java.util.ArrayList;

public class ActiveCompetitions {

    private ArrayList<ActiveCompetitionType> types;

    public ArrayList<ActiveCompetitionType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<ActiveCompetitionType> types) {
        this.types = types;
    }
}
