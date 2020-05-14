package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatType;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.service.ChatService;
import ralmnsk.video.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ralmnsk.video.rtc.Constants.*;


public class CommandAdd extends CommandLogin {
    @Autowired
    private ChatService chatService;

    public CommandAdd(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        String nameRemoteUser = getUserNameFromTextMessage();
        User remoteUser = getUserService().getByLogin(nameRemoteUser);
        User currentUser = getCurrentUser();
        if (remoteUser != null && currentUser != null && !isExistChat(remoteUser, currentUser)){
            createP2PChat(currentUser,remoteUser);
        }
        return new TextMessage("add");
    }

    private String getUserNameFromTextMessage(){
        TextMessage tm = getMessage();
        return getTextMessageData(tm);
    }


}
