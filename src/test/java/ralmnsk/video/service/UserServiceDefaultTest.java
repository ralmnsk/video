package ralmnsk.video.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ralmnsk.video.dao.UserDao;
import ralmnsk.video.dao.UserRepository;
import ralmnsk.video.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceDefaultTest {
    @Autowired
    private UserDao userDao;
    private User user;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setLogin("login");
        userDao.create(user);
    }

    @Test
    void create() {
        User foundUser = userDao.getByLogin("login");
        assertTrue(user.getLogin().equals(foundUser.getLogin()));
    }

    @Test
    void tryCreateWhenAlreadyExists(){
        boolean result = userDao.create(user);
        assertFalse(result);
    }

    @Test
    void getById(){
        Long id = user.getId();
        User byIdUser = userDao.getById(id);
        assertNotNull(byIdUser);
    }

    @Test
    void getByIdNoExists(){
        User byIdUser = userDao.getById(12314L);
        assertNull(byIdUser);
    }

    @Test
    void getByLoginNoExists(){
        User testUser = userDao.getByLogin("noExistsLoginUser");
        assertNull(testUser);
    }

    @Test
    void update(){
        User testUser = userDao.getByLogin("login");
        testUser.setLogin("login2");
        userDao.update(testUser);
        User updatedUser = userDao.getById(testUser.getId());
        assertTrue(updatedUser.getLogin().equals(testUser.getLogin()));
        userDao.delete(updatedUser);
    }

    @Test
    void delete(){
        userDao.delete(user);
        User testUser = userDao.getByLogin("login");
        assertNull(testUser);
    }

    @Test
    void deletNoExists(){
        User testUser = new User();
        testUser.setLogin("noExists");
        testUser.setId(12345L);
        userDao.delete(testUser);
    }

    @AfterEach
    void removeUser(){
        userDao.delete(user);
    }

    @Test
    void testCreate() {
        User foundUser = userService.getByLogin("login");
        assertTrue(user.getLogin().equals(foundUser.getLogin()));
    }

    @Test
    void testGetById() {
    }

    @Test
    void getByLogin() {
    }

    @Test
    void testUpdate() {
    }

    @Test
    void testDelete() {
    }
}