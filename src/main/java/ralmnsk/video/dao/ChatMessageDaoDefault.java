package ralmnsk.video.dao;

import org.springframework.stereotype.Repository;
import ralmnsk.video.model.ChatMessage;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatMessageDaoDefault implements ChatMessageDao{

    private final ChatMessageRepository repo;

    public ChatMessageDaoDefault(ChatMessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean create(ChatMessage msg) {
        ChatMessage savedMsg = repo.save(msg);
        return savedMsg != null;
    }

    @Override
    public ChatMessage getById(Long id) {
        Optional<ChatMessage> msgOptional = repo.findById(id);
        return msgOptional.orElse(null);
    }


    @Override
    public boolean update(ChatMessage msg) {
        ChatMessage updatedMsg = repo.save(msg);
        return updatedMsg != null ;
    }

    @Override
    public boolean delete(ChatMessage msg) {
        repo.delete(msg);
        return repo.findById(msg.getMsgId()).isPresent();
    }

    @Override
    public List<ChatMessage> getByUserId(Long userId) {
        return repo.getByUserId(userId);
    }
}
