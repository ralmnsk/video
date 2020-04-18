package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.service.UserService;

public class CommandHangUp extends CommandLogin {

    public CommandHangUp(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
            WebSocketSession currentSession = getSocketHandler().getCurrentSession();
            if (currentSession != null){
                WebSocketSession remoteSession = getSocketHandler().getPairs().get(currentSession);
                if (remoteSession != null){
                    getSocketHandler().getPairs().remove(currentSession);
                    getSocketHandler().getPairs().remove(remoteSession);
                }
            }
        return new TextMessage("CommandHandUp");
    }
}
