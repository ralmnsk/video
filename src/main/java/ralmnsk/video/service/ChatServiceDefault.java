package ralmnsk.video.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.dao.ChatDao;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.User;

import java.util.List;

@Service
public class ChatServiceDefault implements ChatService{

    private final ChatDao chatDao;

    @Autowired
    public ChatServiceDefault(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @Override
    public boolean create(Chat chat) {
        return chatDao.create(chat);
    }

    @Override
    public Chat getById(Long id) {
        return chatDao.getById(id);
    }

    @Override
    public boolean delete(Chat chat) {
        return chatDao.delete(chat);
    }

    @Override
    public List<Chat> getChats(User user) {
        return chatDao.getChats(user);
    }

    @Override
    public boolean removeUser(User user, Chat chat) {
        return chatDao.removeUser(user, chat);
    }
}
