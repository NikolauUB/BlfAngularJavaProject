package wind.instrument.competitions.middle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

public enum ReservedUsernames {
    RESERVED_USERNAMES;

    @Component
    private static class ReservedUsernamesServiceInjector {
        @Autowired
        @Resource(name = "reservedEmails")
        private Map<String, String> map;

        @PostConstruct
        public void postConstruct() {
            ReservedUsernames.setMap(this.map);
        }
    }

    private static Map<String, String> map;

    public boolean isUsernameReserved(String username) {
        return getMap().containsKey(username);
    }

    public boolean checkEmail(String username, String email) {
        return email.equalsIgnoreCase(getMap().get(username));
    }

    public static void setMap(Map<String, String> map) {
        ReservedUsernames.map = map;
    }

    /**
     * map is initialised from private-bean-data-config.xml through ReservedUsernamesServiceInjector
     */
    private Map<String, String> getMap() {
        return map;
    }
}

