package ralmnsk.video.model;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegistrationForm {
//    @NotNull
//    @Size(min=2,max=30)
    @Pattern(regexp = "[A-Za-z]{2,30}", message = "This is an invalid login")
    private String login;
//    @NotNull
//    @Size(min=2,max=30)
    @Pattern(regexp = "[A-Za-z0-9]{2,30}")
    private String password;

    private String address;
    private String email;


    public RegistrationForm() {
    }

    public RegistrationForm(String login, String password, String address, String email) {
        this.login = login;
        this.password = password;
        this.address = address;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public User toUser(PasswordEncoder encoder){
        User user = new User();

        if(this.getAddress() != null){
            user.setAddress(this.getAddress());
        }
        if(this.getEmail() != null){
            user.setEmail(this.getEmail());
        }
        if(this.getLogin() != null){
            user.setLogin(this.getLogin());
        }
        if(this.getPassword() != null){
            if (encoder != null){
                user.setPassword(encoder.encode(this.getPassword()));
            } else {
                user.setPassword(this.getPassword());
            }
        }
        return user;
    }

    public User toUser(){
        return toUser(null);
    }
}
