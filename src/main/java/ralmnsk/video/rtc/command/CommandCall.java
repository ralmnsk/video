package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandCall extends CommandLogin {

    public CommandCall(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        WebSocketSession currentSession = getSocketHandler().getCurrentSession();
        Map<WebSocketSession, User> sessions = getSocketHandler().getSessions();
        String message = getMessage().getPayload();
        MsgText msg = null;
        try {
            msg = getObjectMapper().readValue(message, MsgText.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(msg != null && msg.getData() != null){
            String remoteUserName = msg.getData();
            List<WebSocketSession> list = getSocketHandler().getSessions().keySet().stream()
                    .filter(s -> sessions.get(s).getLogin() != null)
                    .filter(s -> sessions.get(s).getLogin().equals(remoteUserName))
                    .collect(Collectors.toList());
            if(list.size()>0){
                WebSocketSession remoteUserSession = list.get(0);
                try {
                    getSocketHandler().getPairs().put(currentSession,remoteUserSession);
                    getSocketHandler().getPairs().put(remoteUserSession,currentSession);
                    remoteUserSession.sendMessage(getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new TextMessage("CommandCall");
    }
}
