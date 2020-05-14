package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ralmnsk.video.rtc.Constants.*;


public class CommandFind extends CommandLogin {

    public CommandFind(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
            TextMessage tm = getMessage();
            String find = getTextMessageData(tm) + PERCENT;
            if ((!find.equals(NOTHING))&&(find.length() > 3)){
                List<User> users = getUserService().search(find);
                WebSocketSession currentSession = getSocketHandler().getCurrentSession();
                User user = getSocketHandler().getSessions().get(currentSession);
                if(( users != null )&&(users.size() > 0)){
                    List<String> names = users
                            .stream()
                            .filter(u->!u.getLogin().equals(user.getLogin()))
                            .map(u -> u.getLogin())
                            .collect(Collectors.toList());
                    try {
                        MsgText msg = new MsgText();
                        msg.setEvent(FIND);
                        String namesJson = getObjectMapper().writeValueAsString(names);
                        msg.setData(namesJson);
                        String msgJson = getObjectMapper().writeValueAsString(msg);

                        TextMessage backTm = new TextMessage(msgJson);
                        sendToCurrentSession(getSocketHandler().getCurrentSession(),backTm);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        return new TextMessage("find");
    }

}
