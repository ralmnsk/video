package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.*;

class CommandCallTest {
    private ObjectMapper objectMapper;

    private ModelMapper modelMapper;

    private UserService userService;

    private SocketHandler socketHandler;

    private MessageHandler messageHandler;

    private CommandCall commandCall;

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
        userService = mock(UserServiceDefault.class);
        objectMapper = mock(ObjectMapper.class);
        socketHandler = mock(SocketHandler.class);
        modelMapper = mock(ModelMapper.class);
        commandCall = spy(new CommandCall(objectMapper,modelMapper,userService));
        commandCall.setSocketHandler(socketHandler);

        sessions = new CopyOnWriteLinkedHashMap<>();
        pairs = new CopyOnWriteLinkedHashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("one","one");
        Map<String,Object> mapTwo = new HashMap<>();
        map.put("two","two");
        wsOne = new StandardWebSocketSession(new HttpHeaders(),
                map, new InetSocketAddress("127.0.0.1",8081),new InetSocketAddress("127.0.0.1",8081));
        wsTwo = new StandardWebSocketSession(new HttpHeaders(),
                mapTwo, new InetSocketAddress("127.0.0.1",8081),new InetSocketAddress("127.0.0.1",8081));
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
        commandCall.setMessage(tMessage);

        when(socketHandler.getCurrentSession()).thenReturn(wsOne);
        when(socketHandler.getSessions()).thenReturn(sessions);
        when(socketHandler.getPairs()).thenReturn(pairs);

        doReturn(true).when(commandCall).sendEventToRemoteUser(any(),any());
    }

    @Test
    void executeFreeUser() throws Exception{
        setUp();
        when(objectMapper.readValue(message, MsgText.class)).thenReturn(msg);

        TextMessage execute = commandCall.execute();
        assertTrue(execute.getPayload().equals("CommandCall"));
    }

    @Test
    void executeBusyUser() throws Exception{
        setUp();
        msg.setData("aaa");
        socketHandler.getPairs().remove(wsTwo);
        when(objectMapper.readValue(message, MsgText.class)).thenReturn(msg);
        doReturn(true).when(commandCall).sendEventToRemoteUser(any(),any());

        TextMessage execute = commandCall.execute();
        assertTrue(execute.getPayload().equals("remote user is busy now"));
    }
}