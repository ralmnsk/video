package ralmnsk.video.rtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ralmnsk.video.rtc.command.*;
import ralmnsk.video.rtc.handlers.MessageHandler;
import ralmnsk.video.service.ServiceConfig;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private ServiceConfig serviceConfig;

    public WebSocketConfiguration(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler(),"/socket")
        .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public SocketHandler socketHandler(){
        return new SocketHandler(messageHandler());
    }

    @Bean
    public MessageHandler messageHandler(){
        return new MessageHandler(this);
    }

    @Bean
    public NameCommandGetter nameCommandGetter(){
        return new NameCommandGetter();
    }

    //Commands
    @Bean
    @DependsOn("commandLogin")
    public CommandRegistration commandRegistration(){
        return new CommandRegistration(objectMapper(),modelMapper(), serviceConfig.userService());
    }

    @Bean
    public CommandLogin commandLogin(){
        return new CommandLogin(objectMapper(),modelMapper(),serviceConfig.userService());
    }

    @Bean
    public CommandUsers commandUsers(){
        return new CommandUsers(objectMapper(),modelMapper(),serviceConfig.userService());
    }

    @Bean
    public CommandLogout commandLogout(){
        return new CommandLogout(objectMapper(),modelMapper(),serviceConfig.userService());
    }


}
