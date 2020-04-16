package ralmnsk.video.dao;

import ralmnsk.video.model.User;

import java.util.Optional;

public class UserDaoDefault implements UserDao{

    private final UserRepository repo;

    public UserDaoDefault(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean create(User user) {
        if(user.getLogin() != null){
            User byLoginUser = repo.getByLogin(user.getLogin());
            if(byLoginUser == null){
                User savedUser = repo.save(user);
                if(savedUser != null){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getById(Long id) {
        User user = null;
        Optional<User> userOptional = repo.findById(id);
        if (userOptional.isPresent()){
            user = userOptional.get();
        }
        return user;
    }

    @Override
    public User getByLogin(String login) {
        User user = repo.getByLogin(login);
        return user;
    }

    @Override
    public boolean update(User user) {
        User updatedUser = repo.save(user);
        return updatedUser != null ;
    }

    @Override
    public boolean delete(User user) {
        repo.delete(user);
        return repo.findById(user.getId()).isPresent();
    }
}
