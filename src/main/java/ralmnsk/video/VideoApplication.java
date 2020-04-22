package ralmnsk.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ralmnsk.video.dao.DaoConfig;
import ralmnsk.video.rtc.WebSocketConfiguration;
import ralmnsk.video.service.ServiceConfig;
import ralmnsk.video.service.UserService;
import ralmnsk.video.service.UserServiceDefault;
import ralmnsk.video.web.MvcConfig;
import ralmnsk.video.web.SecurityConfig;

@SpringBootApplication
@Configuration
@Import({/*DaoConfig.class, ServiceConfig.class,
		WebSocketConfiguration.class, MvcConfig.class, */SecurityConfig.class})
public class VideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoApplication.class, args);
//		System.out.println();

	}

}
