package ralmnsk.video.rtc.command;

import org.springframework.web.socket.TextMessage;

public interface Command {
    TextMessage execute();
    void setMessage(TextMessage message);
}
