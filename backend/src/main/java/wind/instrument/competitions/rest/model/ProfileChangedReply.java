package wind.instrument.competitions.rest.model;

import javax.servlet.http.HttpServletResponse;

public class ProfileChangedReply {
    private UserData userData;
    private int code = HttpServletResponse.SC_OK;
    private String errorMsg;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
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
