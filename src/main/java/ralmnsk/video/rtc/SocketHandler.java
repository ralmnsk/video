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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        super.handleTextMessage(session, message);
        currentSession = session;
                System.out.println("handleTextMessage: "+message.getPayload());
//                if(sessions.containsKey(session)){
//                    for(WebSocketSession wsSession:sessions.keySet()){
//                        if(wsSession.isOpen()&&!session.getId().equals(wsSession.getId())){
//                            try {
//                                wsSession.sendMessage(message);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                } else if (pairs.containsKey(session)){
//
//                }

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
//        if(sessions.size()>1){ //when client gets event:start he begins send offer
//            session.sendMessage(new TextMessage("{\"event\": \"start\",\"data\":\"start\"}"));
//        }
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
