package ralmnsk.video.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.dao.ChatMessageDao;
import ralmnsk.video.model.ChatMessage;

import java.util.List;

@Service
@Transactional
public class ChatMessageServiceDefault implements ChatMessageService{

    private ChatMessageDao dao;

    @Autowired
    public ChatMessageServiceDefault(ChatMessageDao dao) {
        this.dao = dao;
    }

    @Override
    public boolean create(ChatMessage msg) {
        return dao.create(msg);
    }

    @Override
    public ChatMessage getById(Long id) {
        return dao.getById(id);
    }

    @Override
    public boolean update(ChatMessage msg) {
        return dao.update(msg);
    }

    @Override
    public boolean delete(ChatMessage msg) {
        return dao.delete(msg);
    }

    @Override
    public List<ChatMessage> getByUserId(Long userId) {
        return dao.getByUserId(userId);
    }

}
