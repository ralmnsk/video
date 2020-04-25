package ralmnsk.video.web;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static ralmnsk.video.rtc.Constants.INDEX;
import static ralmnsk.video.rtc.Constants.MESSAGE;

@Controller
public class ControllerError implements ErrorController{
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    @ExceptionHandler(value=Exception.class)
    public String handleError(Model model,HttpServletRequest request, Exception ex){

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String statusCode = "-";

        if (status != null) {
            HttpStatus httpStatus = resolve((int) status);
            switch(httpStatus) {
                case BAD_REQUEST:
                    statusCode = "400";
                case NOT_FOUND:
                    statusCode = "404";
                case INTERNAL_SERVER_ERROR:
                    statusCode = "500";
            }
        }
        model.addAttribute(MESSAGE,"Error happened, " +
                "error status code:" + statusCode + ", exception: " + ex.getMessage());
        return INDEX;
    }
}
