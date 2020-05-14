package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import ralmnsk.video.dto.ChatDto;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatType;
import ralmnsk.video.model.MsgText;
import ralmnsk.video.model.User;
import ralmnsk.video.service.ChatService;
import ralmnsk.video.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
import static ralmnsk.video.rtc.Constants.*;

//current user gets chats that he enrolled or was enrolled in
public class CommandChats extends CommandLogin {
    @Autowired
    private ChatService chatService;

    public CommandChats(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
//        String key = getKeyMsgText(getMessage());
//        if(isUserLoggedIn(key)){
//            TextMessage msgList = getUserList(key);
//            sendToAll(getSocketHandler().getSessions(), msgList);
//            return msgList;
//        }
//        TextMessage errorMsg = createTextMessage(MESSAGE,USERS_ERROR);
//        sendToCurrentSession(getSocketHandler().getCurrentSession(),errorMsg);
//        return errorMsg;
        String currentUserName = getCurrentUser().getLogin();
        if(currentUserName != null){
            User user = getUserService().getByLogin(currentUserName);
            if(user != null){
                TextMessage chats = findChatsByUserLogin(user);
                sendToCurrentSession(getSocketHandler().getCurrentSession(),chats);
                return chats;
            }
        }

        TextMessage errorMsg = createTextMessage(MESSAGE,USERS_ERROR);
        sendToCurrentSession(getSocketHandler().getCurrentSession(),errorMsg);
        return errorMsg;
    }

    private TextMessage findChatsByUserLogin(User user){
            String msgJson =NOTHING;
            List<Chat> chats = chatService.getChats(user);
            Map<String,String> map = new HashMap<>();//name chat, id chat
            for(Chat chat:chats){
                if (chat.getType().equals(ChatType.P2P)){
                    for (User u: chat.getUsers()){
                        if(!u.getLogin().equals(user.getLogin())){
                            map.put(u.getLogin(),chat.getChatId().toString());
                        }
                    }
                } else {
                    map.put(chat.getChatId().toString(),chat.getChatId().toString());
                }
            }

        if (!map.isEmpty()) {
                    try {
                        String chatsJson = getObjectMapper().writeValueAsString(map);
                        MsgText msg = new MsgText();
                        msg.setEvent(CHATS);//users = chats
                        msg.setData(chatsJson);
                        msgJson = getObjectMapper().writeValueAsString(msg);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            TextMessage textMessage = new TextMessage(msgJson);
        return textMessage;
    }
}
