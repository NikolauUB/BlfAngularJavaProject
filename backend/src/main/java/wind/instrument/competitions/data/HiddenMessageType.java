package wind.instrument.competitions.data;

import java.util.HashMap;
import java.util.Map;

public enum HiddenMessageType {
    HIDDEN_COMPETITION_REQUEST(0),
    HIDDEN_REPLY_TO_COMPETITOR(1),
    HIDDEN_DISLIKED_MESSAGE(2),
    HIDDEN_REPLY_TO_DISLIKED_MESSAGE(3),
    HIDDEN_BY_ADMINISTRATOR(4);


    private int value;
    private static Map<Integer, HiddenMessageType> map = new HashMap<Integer, HiddenMessageType>();

    private HiddenMessageType(int value) {
        this.value = value;
    }
    static {
        for (HiddenMessageType type : HiddenMessageType.values()) {
            map.put(type.value, type);
        }
    }
    public static HiddenMessageType valueOf(int typeIndex) {
        return map.get(typeIndex);
    }


    public int getValue() {
        return value;
    }

}
