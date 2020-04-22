package ralmnsk.video.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ControllerLoginTest {
    @Autowired
    private MockMvc mockMvc;

//    @Test
//    public void loginWithValidUserThenAuthenticated() throws Exception {
//        FormLoginRequestBuilder login = formLogin()
//                .user("user")
//                .password("password");
//
//        mockMvc.perform(login)
//                .andExpect(authenticated().withUsername("user"));
//    }
//
//    @Test
//    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
//        FormLoginRequestBuilder login = formLogin()
//                .user("invalid")
//                .password("invalidpassword");
//
//        mockMvc.perform(login)
//                .andExpect(unauthenticated());
//    }

    @Test
    public void accessUnsecuredResourceThenOk() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk());
    }

    @Test
    public void accessSecuredResourceUnauthenticatedThenRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test

    public void accessSecuredResourceAuthenticatedThenOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}