package ralmnsk.video.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.dao.UserDao;
import ralmnsk.video.dao.UserRepository;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatMessage;
import ralmnsk.video.model.ChatType;
import ralmnsk.video.model.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceDefaultTest {
    @Autowired
    private UserDao userDao;
    private User user;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setLogin("login");
        user.setPassword("noPassword");
        user.setAddress("address");
        user.setEmail("email");
        userDao.create(user);
    }

    @Test
    void create() {
        User foundUser = userDao.getByLogin("login");
        assertTrue(user.getLogin().equals(foundUser.getLogin()));
        userDao.delete(user);
    }

    @Test
    void tryCreateWhenAlreadyExists(){
        boolean result = userDao.create(user);
        assertFalse(result);
        userDao.delete(user);
    }

    @Test
    void getById(){
        Long id = user.getId();
        User byIdUser = userDao.getById(id);
        assertNotNull(byIdUser);
        userDao.delete(user);
    }

    @Test
    void getByIdNoExists(){
        User byIdUser = userDao.getById(12314L);
        assertNull(byIdUser);
        userDao.delete(user);
    }

    @Test
    void getByLoginNoExists(){
        User testUser = userDao.getByLogin("noExistsLoginUser");
        assertNull(testUser);
        userDao.delete(user);
    }

    @Test
    void update(){
        User testUser = userDao.getByLogin("login");
        testUser.setLogin("login2");
        userDao.update(testUser);
        User updatedUser = userDao.getById(testUser.getId());
        assertTrue(updatedUser.getLogin().equals(testUser.getLogin()));
        userDao.delete(updatedUser);
    }

    @Test
    void delete(){
        userDao.delete(user);
        User testUser = userDao.getByLogin("login");
        assertNull(testUser);
    }

    @Test
    void deletNoExists(){
        User testUser = new User();
        testUser.setLogin("noExists");
        testUser.setPassword("noPassword");
        testUser.setId(12345L);
        userDao.delete(testUser);
        userDao.delete(user);
    }

    @AfterEach
    void removeUser(){

    }

    @Test
    void testCreate() {
        User foundUser = userService.getByLogin("login");
        assertTrue(user.getLogin().equals(foundUser.getLogin()));
    }

//    @Transactional
    @Test
    void twoUsersChat() throws Exception{
        //create chat
        User secondUser = new User();
        secondUser.setEmail("mail@mail2.ru");
        secondUser.setAddress("address");
        secondUser.setPassword("noPassword");
        secondUser.setLogin("login2");

        userService.create(secondUser);

        Chat chat = new Chat();
        chat.setType(ChatType.GROUP);
        chat.getUsers().add(user);
        chat.getUsers().add(secondUser);

        user.getChats().add(chat);
        secondUser.getChats().add(chat);

        chatService.create(chat);
        userService.update(user);
        userService.update(secondUser);

        List<Chat> userChats = chatService.getChats(user);
        List<Chat> secondUserChats = chatService.getChats(secondUser);
        assertTrue(userChats.size()>0);
        assertTrue(secondUserChats.size()>0);
//---------------- messages ---------------------------------------

        //find common chat for user and secondUser
        List<Chat> chatsFirst = chatService.getChats(user);
        List<Chat> chatsSecond = chatService.getChats(secondUser);
        List<Chat> commonChats =
                chatsFirst
                        .stream()
                        .map(c -> chatsSecond
                                .stream()
                                .filter(ch -> ch.getChatId().equals(c.getChatId()))
                                .collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        if (commonChats.size() > 0){    //if common chat exists then ...
            Chat commonChat = commonChats.get(0);
            ChatMessage one = new ChatMessage();
            one.setText("one message");
            one.setDate(new Date(System.currentTimeMillis()));
            one.setChat(commonChat);
            one.setUser(user);
            one.setDestination(secondUser.getLogin());

            ChatMessage two = new ChatMessage();
            two.setText("two message");
            two.setDate(new Date(System.currentTimeMillis()));
            two.setChat(commonChat);
            two.setUser(secondUser);
            two.setDestination(user.getLogin());
            chatMessageService.create(one);
            chatMessageService.create(two);

            ChatMessage byIdOne = chatMessageService.getById(one.getMsgId());
            ChatMessage byIdTwo = chatMessageService.getById(two.getMsgId());

            assertTrue(byIdOne.getText().equals(one.getText()));
            assertTrue(byIdTwo.getText().equals(two.getText()));
        } else {
            new Exception("commonChats.size() > 0 ) failed, common chat does not exist");
        }
    }

    @Test
    void removeUserMessage() throws Exception{
        twoUsersChat();
        //find all messages by userId
        List<ChatMessage> messages = chatMessageService.getByUserId(user.getId());
        assertTrue(messages.size() > 0);
        //remove one message
        ChatMessage message = messages.remove(0);
        Long messageId = message.getMsgId();
        chatMessageService.delete(message);

        ChatMessage foundMessage = chatMessageService.getById(messageId);
        assertNull(foundMessage);
    }


    @Test
    void testGetById() {
        User foundUser = userService.getById(user.getId());
        assertTrue(foundUser.getLogin().equals(user.getLogin()));
    }

    @Test
    void getByLogin() {
        User foundUser = userService.getByLogin(user.getLogin());
        assertTrue(foundUser.getLogin().equals(user.getLogin()));
    }

    @Test
    void testUpdate() {
        user.setAddress("new address");
        user.setEmail("newMail@mail.ru");
        userService.update(user);
        User updatedUser = userService.getById(user.getId());
        assertTrue(updatedUser.getAddress().equals("new address"));
        assertTrue(updatedUser.getEmail().equals("newMail@mail.ru"));
    }

    @Test
    void testDelete() {
        Long userId = user.getId();
        userService.delete(user);
        User foundUser = userService.getById(userId);
        assertNull(foundUser);
    }

    @Test
    void removeUserInChat() throws Exception{
        twoUsersChat();
        //delete user: 1.delete user's messages 2. remove user from chat  3. remove char from user
        Long userId = user.getId();
        chatMessageService.getByUserId(userId)
                .stream().forEach(chatMessageService::delete);
        //find common chats for user and second user
        List<Chat> chats = chatService.getChats(user);
        List<Chat> secondChats = chats
                .stream()
                .filter(c->c.getUsers()
                        .stream()
                        .filter(u->u.getLogin().equals("login2")).count()>0)
                .collect(Collectors.toList());
        Chat chat = secondChats.get(0);
        //remove user from chat and chat from user
        chatService.removeUser(user,chat);

        User foundUser = userService.getById(user.getId());
        assertTrue(foundUser.getChats().size() == 0);
        Chat foundChat = chatService.getById(chat.getChatId());
        assertTrue(foundChat.getUsers().size() == 1);
    }

    @Test
    void deleteUser() throws Exception{
        removeUserInChat();
        Long userId = user.getId();
        userService.delete(user);
        User foundUser = userService.getById(userId);
        assertNull(foundUser);
    }

    @Test
    void search() throws Exception{
        List<User> users = userService.search("login");
        assertTrue(users.size() >0 );
        userService.delete(user);
    }

}