package ralmnsk.video.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ralmnsk.video.model.ChatMessage;
import ralmnsk.video.model.User;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("select m from ChatMessage m where m.user.id = :userId")
    List<ChatMessage> getByUserId(@Param("userId") Long userId);
}
