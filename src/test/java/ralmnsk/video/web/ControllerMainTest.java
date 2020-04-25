package ralmnsk.video.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ControllerMainTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ControllerMain controller;

    @Test
    void test() throws Exception{
        assertTrue(controller != null);
    }

    @Test
    void loginPage() throws Exception{
        mvc.perform(get("/login")
                .contentType("html/text"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User Name :")));
    }

    @Test
    void login() throws Exception{
        mvc.perform(post("/login")
                .contentType("html/text"))
                .andExpect(status().isOk());
    }

    @Test
    void registrationPage() throws Exception {
        mvc.perform(get("/registration")
                .contentType("html/text"))
                .andExpect(status().isOk());
    }

    @Test
    void registration() throws Exception{
        mvc.perform(post("/registration")
                .contentType("html/text"))
                .andExpect(status().isOk());
    }

    @Test
    void index() throws Exception{
        mvc.perform(get("/")
                .contentType("html/text"))
                .andExpect(status().isOk());
    }

    @Test
    void chat() throws Exception{
        mvc.perform(get("/chat")
                .contentType("html/text"))
                .andExpect(status().isForbidden());
    }
}