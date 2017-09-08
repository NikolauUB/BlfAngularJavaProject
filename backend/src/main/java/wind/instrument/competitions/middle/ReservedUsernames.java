package wind.instrument.competitions.middle;

import java.util.HashMap;
import java.util.Map;

public enum ReservedUsernames {
    RESERVED_USERNAMES;

    private static Map<String, String> map = new HashMap<String, String>();
    static {
        map.put("nikolayub","not_available");
        
    }

    public boolean isUsernameReserved(String username) {
        return map.containsKey(username);
    }

    public boolean checkEmail(String username, String email) {
        return email.equalsIgnoreCase(map.get(username));
    }
}
