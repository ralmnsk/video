package ralmnsk.video.rtc.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatType;
import ralmnsk.video.model.User;
import ralmnsk.video.service.ChatMessageService;
import ralmnsk.video.service.ChatService;
import ralmnsk.video.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


public class CommandRemove extends CommandLogin {
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatMessageService msgService;
    @Autowired
    private CommandChats commandChats;

    public CommandRemove(ObjectMapper objectMapper, ModelMapper modelMapper, UserService userService) {
        super(objectMapper, modelMapper, userService);
    }

    @Override
    public TextMessage execute() {
        String nameRemoteUser = getUserNameFromTextMessage();
        User remoteUser = getUserService().getByLogin(nameRemoteUser);
        User currentUser = getCurrentUser();
        if (isExistChat(remoteUser, currentUser)){
//            createP2PChat(currentUser,remoteUser);
            removeP2PChat(currentUser,remoteUser);
            commandChats.execute();
        }
        return new TextMessage("remove");
    }

    private String getUserNameFromTextMessage(){
        TextMessage tm = getMessage();
        return getTextMessageData(tm);
    }

    private boolean removeP2PChat(User current, User remote){
        List<Chat> currentChats = chatService.getChats(current);
        if(currentChats != null){
            currentChats = currentChats
                    .stream()
                    .filter(c->c.getType()
                            .equals(ChatType.P2P))
                    .filter(c->c.getUsers().stream()
                            .filter(u->u.getLogin().equals(remote.getLogin()))
                            .count()>0)
                    .collect(Collectors.toList());
            if (!currentChats.isEmpty()){
                Chat chat = currentChats.get(0);
                chatService.removeUser(current,chat);
                chatService.removeUser(remote,chat);
                chatService.delete(chat);
                return true;
            }
        }
        return false;
    }
}
