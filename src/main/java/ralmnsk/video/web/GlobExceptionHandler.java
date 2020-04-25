package ralmnsk.video.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ralmnsk.video.rtc.Constants.INDEX;
import static ralmnsk.video.rtc.Constants.LOGIN;

@ControllerAdvice
public class GlobExceptionHandler {

//    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public void handleNotFoundException(){
        System.out.println("This is a NullPointerException");
    }


}
