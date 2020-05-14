package ralmnsk.video.dao;

import org.hibernate.Session;
import org.hibernate.boot.internal.SessionFactoryBuilderImpl;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.model.Chat;
import ralmnsk.video.model.User;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ChatDaoDefault implements ChatDao{

    private ChatRepository repo;
    private EntityManagerFactory emf;

    public ChatDaoDefault(ChatRepository repo, EntityManagerFactory emf) {
        this.repo = repo;
        this.emf = emf;
    }

    @Transactional
    @Override
    public boolean create(Chat chat) {
                Chat savedChat = repo.save(chat);
        return savedChat != null;
    }

    @Transactional
    @Override
    public Chat getById(Long id) {
        Optional<Chat> chatOptional = repo.findById(id);
        return chatOptional.orElse(null);
    }

    @Transactional
    @Override
    public boolean delete(Chat chat) {
        repo.delete(chat);
        return repo.findById(chat.getChatId()).isPresent();
    }

    @Transactional
    @Override
    public List<Chat> getChats(User user) {
        return repo.getChats(user);
    }

//    @Override
//    public boolean update(Chat chat) {
//        repo.save(chat);
//        return true;U

        @Modifying
        @Override
    public boolean removeUser(User user, Chat chat) {
        EntityManager em = emf.createEntityManager();
        Session session = em.unwrap(Session.class);
//        if(!session.getTransaction().isActive()){
            session.beginTransaction();
//        }
//            Set<User> users = chat.getUsers();
//            List<User> usersFound = users.stream()
//                    .filter(u->u.getLogin()
//                            .equals(user.getLogin()))
//                    .collect(Collectors.toList());
//            if (usersFound.size() > 0){
//                users.remove(usersFound.get(0));
//            }
//
//            Set<Chat> chats = user.getChats();
//            List<Chat> chatsFound = chats.stream()
//                    .filter(c->c.getChatId().equals(chat.getChatId()))
//                    .collect(Collectors.toList());
//            if(chatsFound.size() > 0){
//                chats.remove(chatsFound.get(0));
//            }
//            session.update(user);
//            session.update(chat);
            session.createSQLQuery("delete from user_chat where user_id = ? and chat_id= ? ")
                    .setParameter(1, user.getId())
                    .setParameter(2, chat.getChatId())
                    .executeUpdate();

            session.getTransaction().commit();
            user = session.get(User.class,user.getId());
            chat = session.get(Chat.class,chat.getChatId());
        session.close();
        return true;
    }
}
