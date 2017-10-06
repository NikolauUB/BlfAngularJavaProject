package wind.instrument.competitions.middle;

import javax.annotation.PostConstruct;

public class AdminInfo {

    public static String ADMIN_USERNAME;

    /**
     * it is initialized from private-bean-data-config.xml
     */
    private String adminUsername;

    @PostConstruct
    public void postConstruct() {
        ADMIN_USERNAME = this.getAdminUsername();
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
}
