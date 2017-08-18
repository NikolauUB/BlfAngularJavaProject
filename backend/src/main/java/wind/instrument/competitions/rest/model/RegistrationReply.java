package wind.instrument.competitions.rest.model;


import javax.servlet.http.HttpServletResponse;

public class RegistrationReply {
    private RegistrationData registrationData;
    private AntiBotQuestion newQuestion;
    private int code = HttpServletResponse.SC_OK;
    private String errorMsg;

    public RegistrationData getRegistrationData() {
        return registrationData;
    }

    public void setRegistrationData(RegistrationData registrationData) {
        this.registrationData = registrationData;
    }

    public AntiBotQuestion getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(AntiBotQuestion newQuestion) {
        this.newQuestion = newQuestion;
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
