package wind.instrument.competitions.middle;

import javax.annotation.PostConstruct;

public class AdminInfo {

    public static String ADMIN_USERNAME;
    public static String VK_KEY;

    /**
     * it is initialized from private-bean-data-config.xml
     */
    private String adminUsername;
    private String vkKey;

    @PostConstruct
    public void postConstruct() {
        ADMIN_USERNAME = this.getAdminUsername();
        VK_KEY = this.getVkKey();
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getVkKey() {
        return vkKey;
    }

    public void setVkKey(String vkKey) {
        this.vkKey = vkKey;
    }
}
