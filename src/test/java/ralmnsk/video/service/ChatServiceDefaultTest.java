package ralmnsk.video.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatType;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatServiceDefaultTest {

    private Chat chat;
    private Chat readChat;

    @Autowired
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chat = new Chat();
        chat.setType(ChatType.GROUP);
        chatService.create(chat);
        readChat = chatService.getById(chat.getChatId());
    }

    @AfterEach
    void tearDown() {
        chatService.delete(chat);
    }

    @Test
    void create() {
        assertTrue(readChat != null);
    }

    @Test
    void getById() {
        assertTrue(readChat.getType().equals(ChatType.GROUP));
        assertTrue(readChat.getChatId().equals(chat.getChatId()));
    }

    @Test
    void delete() {
        Long id = chat.getChatId();
        chatService.delete(chat);
        Chat byId = chatService.getById(id);
        assertNull(byId);
    }
}