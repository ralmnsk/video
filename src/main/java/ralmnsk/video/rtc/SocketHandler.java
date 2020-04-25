package ralmnsk.video.rtc;

import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ralmnsk.video.model.User;
import ralmnsk.video.rtc.command.Command;
import ralmnsk.video.rtc.handlers.MessageHandler;
import java.util.Map;

public class SocketHandler extends TextWebSocketHandler {

    private Map<WebSocketSession, User> sessions = new CopyOnWriteLinkedHashMap<>();   //ArrayList<>();
    private Map<WebSocketSession, WebSocketSession> pairs = new CopyOnWriteLinkedHashMap<>();
    private MessageHandler messageHandler;
    private WebSocketSession currentSession;

    @Autowired
    private WebSocketConfiguration config;

    public SocketHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public WebSocketSession getCurrentSession() {
        return currentSession;
    }

    public Map<WebSocketSession, User> getSessions() {
        return sessions;
    }

    public Map<WebSocketSession, WebSocketSession> getPairs() {
        return pairs;
    }

    public void setSessions(Map<WebSocketSession, User> sessions) {
        this.sessions = sessions;
    }

    public void setPairs(Map<WebSocketSession, WebSocketSession> pairs) {
        this.pairs = pairs;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setCurrentSession(WebSocketSession currentSession) {
        this.currentSession = currentSession;
    }

    public void setConfig(WebSocketConfiguration config) {
        this.config = config;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        currentSession = session;
                System.out.println("handleTextMessage: "+message.getPayload());

        Command command = messageHandler.getCommandByMessage(message);
        command.setMessage(message);
        TextMessage messageBack = command.execute();
                System.out.println("messageBack :" + messageBack);
                currentSession = null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        currentSession = session;
        sessions.put(session,new User());
        currentSession = null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        currentSession = session;
        if(sessions.containsKey(session)){

            Command commandLogout = config.commandLogout();
            commandLogout.execute();
        }
        currentSession = null;
    }
}
