package ralmnsk.video.service;

import ralmnsk.video.model.Chat;
import ralmnsk.video.model.User;

import java.util.List;

public interface ChatService {
    boolean create(Chat chat);
    Chat getById(Long id);
    boolean delete(Chat chat);

    List<Chat> getChats(User user);
    boolean removeUser(User user, Chat chat);
}
