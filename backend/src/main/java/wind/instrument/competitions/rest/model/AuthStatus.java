package wind.instrument.competitions.rest.model;


import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class AuthStatus {
    /**
     * code of error in case of authorization errors
     * 400 - bad credentials
     * 500 - server error
     */
    private int code = HttpServletResponse.SC_OK;
    /**
     * error msg if it is server error (code = 500).
     */
    private String errorMsg;
    private Long userId;
    private String username;
    private Boolean autheticated;
    /**
     * user registration time
     */
    private Date created;
    /**
     * current csrf token
     */
    private String token;

    public Boolean getAutheticated() {
        return autheticated;
    }

    public void setAutheticated(Boolean autheticated) {
        this.autheticated = autheticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
