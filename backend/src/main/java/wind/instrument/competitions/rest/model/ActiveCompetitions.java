package wind.instrument.competitions.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
@XmlRootElement
public class ActiveCompetitions {

    private ArrayList<Integer> types;

    public ArrayList<Integer> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Integer> types) {
        this.types = types;
    }
}
