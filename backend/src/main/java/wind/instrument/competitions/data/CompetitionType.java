package wind.instrument.competitions.data;

import java.util.HashMap;
import java.util.Map;


public enum CompetitionType {
    PRESCRIBED_BAROQUE(0),
    PRESCRIBED_JAZZ(1),
    FREE(2),
    COMPOSITION(3);

    private int value;
    private static Map<Integer, CompetitionType> map = new HashMap<Integer, CompetitionType>();

    private CompetitionType(int value) {
        this.value = value;
    }
    static {
        for (CompetitionType type : CompetitionType.values()) {
            map.put(type.value, type);
        }
    }
    public static CompetitionType valueOf(int typeIndex) {
        return map.get(typeIndex);
    }

    public static boolean hasType(Integer typeIndex) {
        if (typeIndex == null) {
            return false;
        }
        return map.containsKey(typeIndex.intValue());
    }

    public int getValue() {
        return value;
    }
}
