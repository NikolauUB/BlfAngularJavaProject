package wind.instrument.competitions.rest.model;

import java.util.LinkedList;

public class RegistrationData {
    private LinkedList<String> givenAnswers;
    private String email;
    private String username;
    private String password;

    public LinkedList<String> getGivenAnswers() {
        return givenAnswers;
    }

    public void setGivenAnswers(LinkedList<String> givenAnswers) {
        this.givenAnswers = givenAnswers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
