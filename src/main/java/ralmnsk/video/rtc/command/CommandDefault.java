package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.service.UserService;

import java.io.IOException;

import static ralmnsk.video.rtc.Constants.NOTHING;

public class CommandDefault extends CommandLogin {


    public CommandDefault(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        System.out.println("Default command");
            WebSocketSession currentSession = getSocketHandler().getCurrentSession();
            if(currentSession != null){
                WebSocketSession remoteSession = getSocketHandler().getPairs().get(currentSession);
                if(remoteSession != null && getMessage() != null && remoteSession.isOpen()){
                    try {
                        remoteSession.sendMessage(getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        return new TextMessage(NOTHING);
    }

}
