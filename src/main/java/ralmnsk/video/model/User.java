package ralmnsk.video.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Table(name="users")
@Entity
public class User {

//    @Column(name="login")
    @NotBlank(message = "login is mandatory")
//    @Pattern(regexp = "[A-Za-z0-9]{2,30}")
    private String login;

//    @Column(name = "password")
    @NotBlank(message = "password is mandatory")
//    @Pattern(regexp = "[A-Za-z0-9]{2,30}")
    private String password;

//    @Column(name="address")
    @NotBlank(message = "address is mandatory")
    private String address;

//    @Column(name="email")
    @NotBlank(message = "email is mandatory")
    private String email;

    @Transient
    private String key;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public User() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
