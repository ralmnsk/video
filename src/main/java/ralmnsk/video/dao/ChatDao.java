package ralmnsk.video.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatMessage;
import ralmnsk.video.model.User;

import java.util.List;
import java.util.Set;

public interface ChatDao {
    boolean create(Chat chat);
    Chat getById(Long id);
    boolean delete(Chat chat);
//    boolean update(Chat chat);
    List<Chat> getChats(User user);
    boolean removeUser(User user, Chat chat);
}
