package wind.instrument.competitions.data;

import java.util.HashMap;
import java.util.Map;

public enum ThemeType {
    SIMPLE(0),
    COMPETITION_REQUEST(1),
    COMPETITION_VOTING(2),
    COMPETITION_RESULT(3);

    private int value;
    private static Map<Integer, ThemeType> map = new HashMap<Integer, ThemeType>();

    private ThemeType(int value) {
        this.value = value;
    }

    static {
        for (ThemeType type : ThemeType.values()) {
            map.put(type.value, type);
        }
    }
    public static ThemeType valueOf(int typeIndex) {
        return map.get(typeIndex);
    }


    public int getValue() {
        return value;
    }
}
