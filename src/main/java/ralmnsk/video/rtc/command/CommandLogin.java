package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.dto.UserDto;
import ralmnsk.video.model.Msg;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.rtc.SocketHandler;
import ralmnsk.video.rtc.WebSocketConfiguration;
import ralmnsk.video.service.UserService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ralmnsk.video.rtc.Constants.*;

public class CommandLogin implements Command {
    private ObjectMapper objectMapper;
    private ModelMapper modelMapper;
    private UserService userService;
    private TextMessage message;

    @Autowired
    private WebSocketConfiguration config;

    @Autowired
    private SocketHandler socketHandler;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public UserService getUserService() {
        return userService;
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public CommandLogin(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public TextMessage getMessage() {
        return message;
    }

    public void setMessage(TextMessage message) {
        this.message = message;
    }

    @Override
    public TextMessage execute() {
        WebSocketSession session = config.socketHandler().getCurrentSession();
        User user = getUser(message);
        System.out.println(user);
        if (user != null && authenticate(user)){
            TextMessage textMessage = loginProcess(user);
            sendToCurrentSession(session,textMessage);
            return textMessage;
        } else {
            TextMessage errorMessage = createTextMessage(MESSAGE,USER_WRONG_LOG_PASS);
            sendToCurrentSession(session,errorMessage);
            return errorMessage;
        }
    }

    public User getUser(TextMessage message){  //getUser for login and registration
        Msg msgJson = null;
        User user = null;
        if(message != null){
            String msg = message.getPayload();
            try {
                msgJson = getObjectMapper().readValue(message.getPayload(), Msg.class);
                UserDto userDto = msgJson.getData();
                user = getModelMapper().map(userDto,User.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public String getKeyMsgText(TextMessage message){ //not for login and registration
        String key = NOTHING;
        if (message != null){
            try {
                MsgText msg = getObjectMapper().readValue(message.getPayload(), MsgText.class);
                key = msg.getKey();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    public boolean authenticate(User user){
        if (user.getLogin() != null && user.getPassword() != null) {
            User userFound = getUserService().getByLogin(user.getLogin());
            if (userFound != null &&
                    user.getLogin().equals(userFound.getLogin()) &&
                    user.getPassword().equals(userFound.getPassword())) {
                    return true;
            }
        }
        return false;
    }

    public TextMessage loginProcess(User user){
            String key = Integer.toString((int)(Math.random()*RATE)); //generate key
            MsgText msgBack = new MsgText();
            msgBack.setEvent(KEY);
            msgBack.setData(key);
            user.setKey(key);
            getSocketHandler().getSessions().put(getSocketHandler().getCurrentSession(),user);
        return msgToTextMessage(msgBack);
    }

    public TextMessage msgToTextMessage(MsgText msgText){
        String msgToJson = NOTHING; //it is an description of the user creation
        try {
            msgToJson = getObjectMapper().writeValueAsString(msgText);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        TextMessage textMessage = new TextMessage(msgToJson);
        return textMessage;
    }

    public TextMessage createTextMessage(String event, String data){
            MsgText msgText = new MsgText();
            msgText.setData(data);
            msgText.setEvent(MESSAGE);
        return msgToTextMessage(msgText);
    }

    public boolean isUserLoggedIn(String key){
        List<User> users = null;
        if(key != null){
            users = getSocketHandler().getSessions().values()
                    .stream().filter(u->u.getKey() != null)
                    .filter(u->u.getKey().equals(key))
                    .collect(Collectors.toList());
        }
        return users!=null && users.size()>0;
    }

    public TextMessage getUserList(String key){
//            Collection<String> users = getUsersHandler().getKeys().values();
            List<String> users = getSocketHandler().getSessions().values()
                    .stream().filter(u->u.getLogin() != null)
                    .map(u->u.getLogin()).collect(Collectors.toList());
            String usersJson = NOTHING;
            String msgJson = NOTHING;
            MsgText msg = new MsgText();
            msg.setEvent(USERS);
            msg.setKey(key);
        try {
            usersJson = getObjectMapper().writeValueAsString(users);
            msg.setData(usersJson);
            msgJson = getObjectMapper().writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        TextMessage textMessage = new TextMessage(msgJson);
        return textMessage;
    }

    public void sendToCurrentSession(WebSocketSession session, TextMessage backTextMessage){
        try {
            session.sendMessage(backTextMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(Map<WebSocketSession,User> sessions, TextMessage backTextMessage){
        sessions.keySet().stream().forEach(s-> {
            try {
                s.sendMessage(backTextMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}