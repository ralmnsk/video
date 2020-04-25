package ralmnsk.video.web;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ralmnsk.video.model.User;
import ralmnsk.video.model.UserPrincipal;
import ralmnsk.video.service.ServiceConfig;

import javax.servlet.http.HttpSession;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ControllerLoginTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ControllerMain controllerMain;

    private UserDetails userDetails;

    @Test
    public void controllerNotNull(){
        assertNotNull(controllerMain);
    }


    public void setup(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setPassword(encoder.encode("qqq"));
        user.setLogin("qqq");
        user.setEmail("qqq@mail.ru");
        user.setAddress("qqq");
        userDetails = new UserPrincipal(user);

        when(userDetailsService.loadUserByUsername("qqq")).thenReturn(userDetails);
    }

    @Test
    @WithMockUser(username = "qqq", password = "qqq", roles = "USER")
    public void loginAuthenticated() throws Exception {
        setup();
        mockMvc
                .perform(get("/login")
                        .with(user(userDetails)))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "qqq", password = "qqq", roles = "USER")
    public void chatAuthenticated() throws Exception {
        setup();
        mockMvc
                .perform(get("/chat")
                        .with(user(userDetails)))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void chatUnauthenticated() throws Exception{
        mockMvc
                .perform(get("/chat"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = formLogin()
                .user("qqq111")
                .password("qqq111");

        mockMvc.perform(login)
                .andExpect(unauthenticated());
    }

    @Test
    public void accessToRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk());
    }


    @Test
    public void accessToIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}