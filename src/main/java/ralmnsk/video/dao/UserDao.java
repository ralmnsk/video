package ralmnsk.video.dao;

import ralmnsk.video.model.User;

public interface UserDao {
    boolean create(User user);
    User getById(Long id);
    User getByLogin(String login);
    boolean update(User user);
    boolean delete(User user);
}
