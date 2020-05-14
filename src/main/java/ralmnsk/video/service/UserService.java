package ralmnsk.video.service;

import ralmnsk.video.model.User;

import java.util.List;

public interface UserService {
    boolean create(User user);
    User getById(Long id);
    User getByLogin(String login);
    boolean update(User user);
    boolean delete(User user);
    boolean isRegistered(User user);
    List<User> search(String find);
}
