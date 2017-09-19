package wind.instrument.competitions.rest.model;


import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class AuthStatus {
    /**
     * code of error in case of authorization errors
     * 400 - bad credentials
     * 500 - server error
     */
    private int cd = HttpServletResponse.SC_OK;
    /**
     * error msg if it is server error (code = 500).
     */
    private String eMsg;
    private Long uId;
    private String uName;
    private Boolean auth;
    private String tkn;



    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }


    public String geteMsg() {
        return eMsg;
    }

    public void seteMsg(String eMsg) {
        this.eMsg = eMsg;
    }

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    /**
    /**
     * current csrf token
     */
    public String getTkn() {
        return tkn;
    }

    public void setTkn(String tkn) {
        this.tkn = tkn;
    }
}
