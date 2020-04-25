package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.rtc.SocketHandler;
import ralmnsk.video.service.UserService;
import ralmnsk.video.service.UserServiceDefault;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandDefaultTest {

    private ObjectMapper objectMapper;

    private ModelMapper modelMapper;

    private UserService userService;

    private SocketHandler socketHandler;

    private MessageHandler messageHandler;

    private CommandDefault commandDefault;

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

        userService = mock(UserServiceDefault.class);
        objectMapper = mock(ObjectMapper.class);
        socketHandler = mock(SocketHandler.class);
        modelMapper = mock(ModelMapper.class);
        commandDefault = spy(new CommandDefault(objectMapper,modelMapper,userService));
        commandDefault.setSocketHandler(socketHandler);

        sessions = new CopyOnWriteLinkedHashMap<>();
        pairs = spy(new CopyOnWriteLinkedHashMap<>());
        Map<String,Object> map = new HashMap<>();
        map.put("one","one");
        Map<String,Object> mapTwo = new HashMap<>();
        map.put("two","two");
//        wsOne = spy(new StandardWebSocketSession(new HttpHeaders(),
//                map, new InetSocketAddress("127.0.0.1",8081),new InetSocketAddress("127.0.0.1",8081)));
//        wsTwo = spy(new StandardWebSocketSession(new HttpHeaders(),
//                mapTwo, new InetSocketAddress("127.0.0.1",8081),new InetSocketAddress("127.0.0.1",8081)));

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
        commandDefault.setMessage(tMessage);

        when(socketHandler.getCurrentSession()).thenReturn(wsOne);
        when(socketHandler.getSessions()).thenReturn(sessions);
        when(socketHandler.getPairs()).thenReturn(pairs);
        when(pairs.get(wsOne)).thenReturn(wsTwo);//

        doReturn(true).when(commandDefault).sendEventToRemoteUser(any(),any());
    }

    @Test
    void execute() throws Exception{
        setUp();
        when(objectMapper.readValue(message, MsgText.class)).thenReturn(msg);
        TextMessage execute = commandDefault.execute();

        assertTrue(execute.getPayload().equals(""));
    }
}