package ralmnsk.video.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.dao.UserDao;
import ralmnsk.video.model.User;
import ralmnsk.video.model.UserPrincipal;

@Service
public class UserServiceDefault implements UserService{

    private final UserDao userDao;

    @Autowired
    public UserServiceDefault(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public boolean create(User user) {
        return userDao.create(user);
    }

    @Override
    @Transactional
    public User getById(Long id) {
        return userDao.getById(id);
    }

    @Override
    @Transactional
    public User getByLogin(String login) {
        return userDao.getByLogin(login);
    }

    @Override
    @Transactional
    public boolean update(User user) {
        return userDao.update(user);
    }

    @Override
    @Transactional
    public boolean delete(User user) {
        return userDao.delete(user);
    }

    @Override
    public boolean isRegistered(User user) {
        User foundUser = userDao.getByLogin(user.getLogin());
        return foundUser != null;
    }
}
