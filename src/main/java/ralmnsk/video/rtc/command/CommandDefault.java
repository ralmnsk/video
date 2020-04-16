package ralmnsk.video.rtc.command;

import org.springframework.web.socket.TextMessage;

import static ralmnsk.video.rtc.Constants.NOTHING;

public class CommandDefault implements Command {
    @Override
    public TextMessage execute() {
        System.out.println("Default command");
        return new TextMessage(NOTHING);
    }

    @Override
    public void setMessage(TextMessage message) {

    }
}
