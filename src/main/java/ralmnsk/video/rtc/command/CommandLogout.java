package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.User;
import ralmnsk.video.service.UserService;

import java.util.Map;

public class CommandLogout extends CommandLogin {

    public CommandLogout(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        Map<WebSocketSession,User> sessions = getSocketHandler().getSessions();
        WebSocketSession currentSession = getSocketHandler().getCurrentSession();
        User user = sessions.get(currentSession);
        //send updated users list
        if (user != null && user.getLogin() != null){
            String key = user.getKey();
            if (key != null){
                sessions.keySet()
                        .stream()
                        .filter(k->sessions.get(k).getLogin()!=null)
                        .filter(k->sessions.get(k).getLogin().equals(user.getLogin()))
                        .forEach(k->sessions.remove(k));
                TextMessage msgList = getUserList(key);
                sendToAll(getSocketHandler().getSessions(), msgList);
            }
        }
        //remove closed session
        sessions.remove(currentSession);
        return new TextMessage("logout");
    }
}
