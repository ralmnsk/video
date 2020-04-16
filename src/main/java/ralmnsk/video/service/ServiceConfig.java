package ralmnsk.video.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ralmnsk.video.dao.DaoConfig;

@Configuration
public class ServiceConfig {

    private DaoConfig daoConfig;

    public ServiceConfig(DaoConfig daoConfig){
        this.daoConfig = daoConfig;
    }

    @Bean
    public UserService userService(){
        return new UserServiceDefault(daoConfig.userDao());
    }
}
