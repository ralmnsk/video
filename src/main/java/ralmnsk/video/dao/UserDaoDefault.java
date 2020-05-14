package ralmnsk.video.dao;

import org.springframework.stereotype.Repository;
import ralmnsk.video.model.User;

import java.util.List;
import java.util.Optional;

@Repository
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
            } else if (byLoginUser != null){
                Long id = repo.getByLogin(user.getLogin()).getId();
                user.setId(id);
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
        try {
            User updatedUser = repo.save(user);
        } catch (Exception e){
            System.out.println("exception happened during update operation "+ e);
            return false;
        }
            return true;
    }

    @Override
    public boolean delete(User user) {
        repo.delete(user);
        return repo.findById(user.getId()).isPresent();
    }

    @Override
    public List<User> search(String find) {
        return repo.search(find);
    }
}
