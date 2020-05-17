package ralmnsk.video.rtc.handlers;

import org.springframework.web.socket.TextMessage;
import ralmnsk.video.rtc.WebSocketConfiguration;
import ralmnsk.video.rtc.command.Command;
import ralmnsk.video.rtc.command.CommandDefault;

import static ralmnsk.video.rtc.Constants.*;


public class MessageHandler {

    private WebSocketConfiguration config;

    public MessageHandler(WebSocketConfiguration config) {
        this.config = config;
    }

    public Command getCommandByMessage(TextMessage message){
        Command command = config.commandDefault();
        if(message.getPayload() != null ){
        String stringCommand = config.nameCommandGetter().parse(message.getPayload());
            switch (stringCommand){
                case REGISTER:
                    command = config.commandRegistration();
                    break;
                case LOGIN:
                    command = config.commandLogin();
                    break;
                case CHATS:
                    command = config.commandChats();
                    break;
                case CALL:
                    command = config.commandCall();
                    break;
                case HANG_UP:
                    command = config.commandHangUp();
                    break;
                case FIND:
                    command = config.commandFind();
                    break;
                case ADD:
                    command = config.commandAdd();
                    break;
                case REMOVE:
                    command = config.commandRemove();
                    break;
                default:
                    break;
            }
        }
        return command;
    }
}
