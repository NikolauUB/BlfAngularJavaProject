package wind.instrument.competitions.middle;

public class LoginDataObject {
    private String username;
    private String password;
    public LoginDataObject(){
    }

    public LoginDataObject(String anUsername, String aPassword){
        this.username = anUsername;
        this.password = aPassword;
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
