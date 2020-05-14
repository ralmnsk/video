package ralmnsk.video.service;

import ralmnsk.video.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    boolean create(ChatMessage msg);
    ChatMessage getById(Long id);
    boolean update(ChatMessage msg);
    boolean delete(ChatMessage msg);
    List<ChatMessage> getByUserId(Long userId);
}
