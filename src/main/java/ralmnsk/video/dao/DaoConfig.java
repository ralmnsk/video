package ralmnsk.video.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(HibernateConfiguration.class)
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ralmnsk.video.dao")
@ComponentScan
public class DaoConfig {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Bean
//    public UserDao userDao() {
//        return new UserDaoDefault(userRepository);
//    }
}
