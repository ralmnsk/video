package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.rtc.SocketHandler;
import ralmnsk.video.rtc.WebSocketConfiguration;
import ralmnsk.video.service.UserService;
import ralmnsk.video.service.UserServiceDefault;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class CommandLoginTest {
    private ObjectMapper objectMapper;

    private ModelMapper modelMapper;

    private UserService userService;

    private SocketHandler socketHandler;

    private MessageHandler messageHandler;

    private CommandLogin command;

    private WebSocketConfiguration config;

    private Map<WebSocketSession, User> sessions;
    private Map<WebSocketSession, WebSocketSession> pairs;
    private WebSocketSession wsOne;
    private WebSocketSession wsTwo;
    private User userOne;
    private User userTwo;
    private String message;
    private MsgText msg;
    private TextMessage tMessage;

    public void setUp(){
        MockitoAnnotations.initMocks(this);
        wsOne = mock(WebSocketSession.class);
        wsTwo = mock(WebSocketSession.class);
        config = mock(WebSocketConfiguration.class);

        userService = mock(UserServiceDefault.class);
        objectMapper = new ObjectMapper();
        socketHandler = mock(SocketHandler.class);
        modelMapper = mock(ModelMapper.class);
        command = spy(new CommandHangUp(objectMapper,modelMapper,userService));
        command.setSocketHandler(socketHandler);

        sessions = new CopyOnWriteLinkedHashMap<>();
        pairs = spy(new CopyOnWriteLinkedHashMap<>());
        Map<String,Object> map = new HashMap<>();
        map.put("one","one");
        Map<String,Object> mapTwo = new HashMap<>();
        mapTwo.put("two","two");

        userOne = new User();
        userOne.setLogin("qqq");
        userOne.setPassword("qqq");
        userTwo = new User();
        userTwo.setLogin("aaa");
        userTwo.setPassword("aaa");
        sessions.put(wsTwo, userTwo);
        sessions.put(wsOne,userOne);
        pairs.put(wsOne, wsTwo);
        pairs.put(wsTwo,wsOne);

        message = "text message";

        msg = new MsgText();
        msg.setData("qqq");
        msg.setEvent("call");

        tMessage = new TextMessage("text message");
        command.setMessage(tMessage);

        when(socketHandler.getCurrentSession()).thenReturn(wsOne);
        when(socketHandler.getSessions()).thenReturn(sessions);
        when(socketHandler.getPairs()).thenReturn(pairs);
        when(config.socketHandler()).thenReturn(socketHandler);
        when(command.getUser(any())).thenReturn(userTwo);
        when(command.loginProcess(userTwo)).thenReturn(tMessage);
        when(userService.getByLogin(userTwo.getLogin())).thenReturn(userTwo);
    }

    @Test
    void execute() throws Exception{
        setUp();
        TextMessage execute = command.execute();

        assertTrue(execute.getPayload().equals("CommandHandUp"));
    }

    @Test
    void getUser() {
    }

    @Test
    void getKeyMsgText() {
    }

    @Test
    void authenticate() {
        setUp();
        when(userService.getByLogin("qqq")).thenReturn(userOne);

        assertTrue(command.authenticate(userOne));
    }

    @Test
    void loginProcess() {
        setUp();

        TextMessage tm = command.loginProcess(userOne);
        System.out.println(tm.getPayload());
        String test = tm.getPayload();

        assertTrue(test.contains("{\"event\":\"key\",\"data\":"));
    }

    @Test
    void msgToTextMessage() {
//        objectMapper = new ObjectMapper();
//        command.setObjectMapper(objectMapper);
        setUp();
        MsgText msg = new MsgText();
        msg.setData("testData");
        msg.setEvent("testEvent");
        TextMessage tm = command.msgToTextMessage(msg);
        System.out.println(tm.getPayload());
        String test = tm.getPayload();
        assertTrue(test.equals("{\"event\":\"testEvent\",\"data\":\"testData\",\"key\":null}"));
    }

    @Test
    void createTextMessage() {
    }

    @Test
    void isUserLoggedIn() {
    }

    @Test
    void getUserList() {
    }

    @Test
    void sendToCurrentSession() {
    }

    @Test
    void sendToAll() {
    }

    @Test
    void sendEventToRemoteUser() {
    }
}