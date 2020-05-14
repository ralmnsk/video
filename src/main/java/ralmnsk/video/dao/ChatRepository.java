package ralmnsk.video.dao;


import org.hibernate.annotations.NamedNativeQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.ChatMessage;
import ralmnsk.video.model.User;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select c from Chat c where :user member of c.users")
    List<Chat> getChats(@Param("user") User user);

    @Query("select c from Chat c where :id = c.chatId ")
    Set<ChatMessage> getMessages(@Param("id") Long id);

////    @Query("delete from User.chats c where c.chatId = :chatId")
//    @Query("delete from Chat.users u where u.id = :userId ")
//    @Modifying
//    void removeUser(@Param("userId") Long userId, @Param("chatId") Long chatId);
}
