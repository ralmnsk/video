package ralmnsk.video.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ralmnsk.video.model.RegistrationForm;
import ralmnsk.video.model.User;
import ralmnsk.video.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ralmnsk.video.rtc.Constants.*;

@Controller
public class ControllerMain {
    @Autowired
    private ApplicationContext context;

    private PasswordEncoder encoder;
    private UserService userService;
    private UserDetailsService userDetailsService;

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    @Qualifier("uds")
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public ControllerMain() {
    }

    @ModelAttribute("registrationForm")
    public RegistrationForm registrationForm(){
        return new RegistrationForm();
    }


    @GetMapping("/login")
    public String loginPage(){
        return LOGIN;
    }

    @PostMapping("/login")
    public String login(@Valid RegistrationForm form, Model model, HttpServletRequest req){
        User user = form.toUser();
        UserDetails userDetails = null;
        try{
            userDetails = userDetailsService.loadUserByUsername(user.getLogin());
        } catch (UsernameNotFoundException e){
            System.out.println(e);
        }
        if(userDetails != null){
            if(encoder.matches(user.getPassword(),userDetails.getPassword())){
                User userLogged = userService.getByLogin(user.getLogin());
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(userLogged.getLogin(),
                                userLogged.getPassword(),userDetails.getAuthorities()));
                req.getSession().setAttribute(LOGIN, userLogged.getLogin());
                req.getSession().setAttribute(PASSWORD, userLogged.getPassword());
                System.out.println(LOGIN_SUCCESS);
                model.addAttribute(MESSAGE,LOGIN_SUCCESS);
                return "redirect:/chat";
            }
        }
            model.addAttribute(MESSAGE,LOGIN_PASS_INCORRECT);
        return LOGIN;
    }

    @GetMapping("/registration")
    public String registrationPage(){
        return REGISTRATION;
    }

    @PostMapping("/registration")
    public String registration(@Valid RegistrationForm form, Model model){
            User user =form.toUser(encoder);
            if (user.getLogin() != null && user.getPassword() != null) {
                User userFound = userService.getByLogin(user.getLogin());
                if (userFound != null &&
                        user.getLogin().equals(userFound.getLogin())) {
                        model.addAttribute(MESSAGE,"User was created before");
                    System.out.println("User was created before");
                    return REGISTRATION;
                }
                userService.create(user);
                model.addAttribute(MESSAGE,"User " + user.getLogin() + " was created successful");
            }
        return LOGIN;
    }

    @GetMapping("/")
    public String index(){
        return INDEX;
    }

    @GetMapping("/chat")
    public String chat(){
        return CHAT;
    }

}
