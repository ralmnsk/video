package ralmnsk.video.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ralmnsk.video.dao.UserDao;
import ralmnsk.video.dao.UserDaoDefault;
import ralmnsk.video.dao.UserRepository;
import ralmnsk.video.model.User;
import ralmnsk.video.model.UserPrincipal;

@Service("uds")
public class UserDetailsServiceDefault implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public
    UserDetailsServiceDefault(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userDao.getByLogin(userName);
        if (user == null) {
            throw new UsernameNotFoundException("UsernameNotFountException "+userName);
        }
        return new UserPrincipal(user);
    }
}
