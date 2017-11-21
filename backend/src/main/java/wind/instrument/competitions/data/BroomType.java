package wind.instrument.competitions.data;

import java.util.HashMap;
import java.util.Map;

public enum BroomType {
    NONE(0),
    FIRST_BROOM(110),
    SECOND_BROOM(220),
    THIRD_BROOM(440),
    FORTH_BROOM(880);

    private int value;
    private static Map<Integer, BroomType> map = new HashMap<Integer, BroomType>();

    private BroomType(int value) {
        this.value = value;
    }

    static {
        for (BroomType type : BroomType.values()) {
            map.put(type.value, type);
        }
    }

    public static BroomType valueOf(int typeIndex) {
        return map.get(typeIndex);
    }


    public int getValue() {
        return value;
    }

}
