package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import ralmnsk.video.model.User;
import ralmnsk.video.service.UserService;
import static ralmnsk.video.rtc.Constants.*;

public class CommandRegistration extends CommandLogin {

    public CommandRegistration(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }


    @Override
    public TextMessage execute() {
            User user = getUser(getMessage());
                if (user != null && !authenticate(user)){
                    boolean isUserCreated = getUserService().create(user);
                    if(isUserCreated){
                        TextMessage msg = createTextMessage(MESSAGE,USER_CREATED);
                        sendToCurrentSession(getSocketHandler().getCurrentSession(),msg);
                        return msg;
                    }
                }
        TextMessage errorMsg = createTextMessage(MESSAGE,USER_ALREADY_EXISTS);
        sendToCurrentSession(getSocketHandler().getCurrentSession(),errorMsg);
        return errorMsg;
    }
}
