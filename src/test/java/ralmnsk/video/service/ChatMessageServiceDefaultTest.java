package ralmnsk.video.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ralmnsk.video.model.ChatMessage;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatMessageServiceDefaultTest {

    private ChatMessage msg;
    private ChatMessage readMsg;

    @Autowired
    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp() {
        msg = new ChatMessage();
        msg.setDate(new Date(System.currentTimeMillis()));
        msg.setText("Text message");
        chatMessageService.create(msg);
        readMsg = chatMessageService.getById(msg.getMsgId());
    }

    @AfterEach
    void tearDown() {
        chatMessageService.delete(msg);
    }

    @Test
    void create() {
        assertTrue(msg.getText().equals("Text message"));
    }

    @Test
    void getById() {
        assertTrue(readMsg.getText().equals("Text message"));
    }

    @Test
    void update() {
        msg.setText("New text message");
        chatMessageService.update(msg);
        ChatMessage newMsg = chatMessageService.getById(msg.getMsgId());
        assertTrue(newMsg.getText().equals("New text message"));
    }

    @Test
    void delete() {
        Long id = msg.getMsgId();
        chatMessageService.delete(msg);
        ChatMessage byId = chatMessageService.getById(id);
        assertNull(byId);
    }
}