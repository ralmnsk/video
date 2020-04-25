package ralmnsk.video.model;

import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class RegistrationFormTest {
    private static Validator validator;
    private RegistrationForm form;


    public void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new RegistrationForm();
    }

    @Test
    void setLoginIncorrect() throws Exception{
        setUp();
        form.setLogin("!");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validate(form);

        assertTrue(constraintViolations.size() > 0);
    }

    @Test
    void setLoginCorrect() throws Exception{
        setUp();
        form.setLogin("qqq");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validate(form);

        assertTrue(constraintViolations.size() == 0);
    }

    @Test
    void setPasswordIncorrect() {
        setUp();
        form.setPassword("111111111111111111111111111111111111111111111111111111111111");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validate(form);

        assertTrue(constraintViolations.size() > 0);
    }

    @Test
    void setPasswordCorrect() {
        setUp();
        form.setPassword("qqq");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validate(form);

        assertTrue(constraintViolations.size() == 0);
    }


    @Test
    void toUser() {
        setUp();
        form.setLogin("qqq");
        User user = form.toUser();
        assertTrue(user.getLogin().equals("qqq"));
    }
}