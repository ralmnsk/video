package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import ralmnsk.video.service.UserService;

import static ralmnsk.video.rtc.Constants.*;

public class CommandUsers extends CommandLogin {


    public CommandUsers(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        String key = getKeyMsgText(getMessage());
        if(isUserLoggedIn(key)){
            TextMessage msgList = getUserList(key);
            sendToAll(getSocketHandler().getSessions(), msgList);
            return msgList;
        }
        TextMessage errorMsg = createTextMessage(MESSAGE,USERS_ERROR);
        sendToCurrentSession(getSocketHandler().getCurrentSession(),errorMsg);
        return errorMsg;
    }
}
