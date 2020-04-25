package ralmnsk.video.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ralmnsk.video.dao.UserDao;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserDetailsServiceDefaultTest {

    @Autowired
    private UserDao userDao;

    @Test
    void loadUserByUsername() throws Exception{
        UserDetailsServiceDefault uds = new UserDetailsServiceDefault(userDao);
        UserDetails userDetails = uds.loadUserByUsername("qqq");
        SimpleGrantedAuthority authority=new SimpleGrantedAuthority("USER");
        UserDetails ud = uds.loadUserByUsername("qqq");

        assertTrue(userDetails.getUsername().equals("qqq"));
        assertTrue(ud.getAuthorities().contains(authority));
    }

    @Test
    void loadUserByUsernameFalse() throws Exception{
        UserDetailsServiceDefault uds = new UserDetailsServiceDefault(userDao);
        Exception exception = assertThrows(UsernameNotFoundException.class,()->uds.loadUserByUsername("qqq111"));
        assertTrue(exception.getMessage().contains("UsernameNotFountException"));
    }
}