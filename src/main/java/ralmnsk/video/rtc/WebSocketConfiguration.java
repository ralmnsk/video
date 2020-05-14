package ralmnsk.video.rtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ralmnsk.video.rtc.command.*;
import ralmnsk.video.rtc.handlers.MessageHandler;
import ralmnsk.video.service.ServiceConfig;
import ralmnsk.video.service.UserService;


@Configuration
@EnableWebSocket
@ComponentScan({"ralmnsk.video.rtc"})
@Import(ServiceConfig.class)
public class WebSocketConfiguration implements WebSocketConfigurer {

    private UserService userService;

    @Autowired
    public WebSocketConfiguration(UserService userService) {
        this.userService = userService;
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
        return new CommandRegistration(objectMapper(),modelMapper(), userService);
    }

    @Bean
    public CommandLogin commandLogin(){
        return new CommandLogin(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandChats commandChats(){
        return new CommandChats(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandLogout commandLogout(){
        return new CommandLogout(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandCall commandCall(){
        return new CommandCall(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandDefault commandDefault(){
        return new CommandDefault(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandHangUp commandHangUp(){
        return new CommandHangUp(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandFind commandFind(){
        return new CommandFind(objectMapper(),modelMapper(),userService);
    }

    @Bean
    public CommandAdd commandAdd(){
        return new CommandAdd(objectMapper(),modelMapper(),userService);
    }
}
