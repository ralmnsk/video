package ralmnsk.video.dao;

import org.springframework.data.repository.query.Param;
import ralmnsk.video.model.ChatMessage;

import java.util.List;

public interface ChatMessageDao {
    boolean create(ChatMessage msg);
    ChatMessage getById(Long id);
    boolean update(ChatMessage msg);
    boolean delete(ChatMessage msg);
    List<ChatMessage> getByUserId(Long userId);
}
