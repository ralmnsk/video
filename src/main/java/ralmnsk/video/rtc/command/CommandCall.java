package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.rtc.SocketHandler;
import ralmnsk.video.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ralmnsk.video.rtc.Constants.CALL;
import static ralmnsk.video.rtc.Constants.HANG_UP;

public class CommandCall extends CommandLogin {
    private SocketHandler socketHandler = getSocketHandler();

    public void setSocketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }


    public CommandCall(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        WebSocketSession currentSession = socketHandler.getCurrentSession();
        Map<WebSocketSession, User> sessions = socketHandler.getSessions();
        Map<WebSocketSession, WebSocketSession> pairs = socketHandler.getPairs();
        String message = getMessage().getPayload();
        MsgText msg = null;
        try {
            msg = getObjectMapper().readValue(message, MsgText.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(msg != null && msg.getData() != null){
            String remoteUserName = msg.getData();
            List<WebSocketSession> list = socketHandler.getSessions().keySet().stream()
                    .filter(s -> sessions.get(s).getLogin() != null)
                    .filter(s -> sessions.get(s).getLogin().equals(remoteUserName))
                    .collect(Collectors.toList());
            if(list.size()>0 ){
                WebSocketSession remoteUserSession = list.get(0);
                    if ((!pairs.containsKey(currentSession)&&!pairs.containsKey(remoteUserSession))){
                        pairs.put(currentSession,remoteUserSession);
                        pairs.put(remoteUserSession,currentSession);
                        sendEventToRemoteUser(CALL,remoteUserSession);
                    } else if(pairs.containsKey(currentSession)&&pairs.containsKey(remoteUserSession)){
                        sendEventToRemoteUser(CALL,remoteUserSession);
                    }
                    else {
                        sendEventToRemoteUser(HANG_UP,currentSession);
                        return new TextMessage("remote user is busy now");
                    }
            }
        }

        return new TextMessage("CommandCall");
    }



}
