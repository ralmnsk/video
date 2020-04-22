package ralmnsk.video.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import ralmnsk.video.dao.DaoConfig;

@Configuration
@ComponentScan({"ralmnsk.video.service"})
@Import(DaoConfig.class)
public class ServiceConfig {

//    private DaoConfig daoConfig;
//
//    public ServiceConfig(DaoConfig daoConfig){
//        this.daoConfig = daoConfig;
//    }
//
//    @Bean
//    public UserService userService(){
//        return new UserServiceDefault(daoConfig.userDao());
//    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        return new UserDetailsServiceDefault(daoConfig.userDao());
//    }
}
