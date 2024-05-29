package avlyakulov.timur.CloudFileStorage.util.validator;

import avlyakulov.timur.CloudFileStorage.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginAndPasswordValidatorTest {
    private LoginAndPasswordValidator validator;
    private UserDto userUnderTest;

    @BeforeEach
    public void setUp() {
        validator = new LoginAndPasswordValidator();
        userUnderTest = new UserDto("timur", "KLJsmajxhjsjs2", "KLJsmajxhjsjs2");
    }

    @Test
    public void validate_noErrors_userIsValid() {
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getAllErrors().isEmpty()).isTrue();
    }

    @Test
    public void validate_loginError_userLongIsBlank() {
        userUnderTest.setLogin("      ");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("login")).isEqualTo(1);
        assertThat(errors.getFieldError("login").getDefaultMessage())
                .isEqualTo("Your login can't be null or empty. Enter valid login");
    }

    @Test
    public void validate_noErrors_userEmailIsValid() {
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertTrue(errors.getAllErrors().isEmpty());
    }

    @Test
    public void validate_loginError_userEmailNotValid() {
        userUnderTest.setLogin("timur@gmail");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("login")).isEqualTo(1);
        assertThat(errors.getFieldError("login").getDefaultMessage())
                .isEqualTo("Your email isn't valid. Please enter valid email");
    }

    @Test
    public void validate_loginError_lengthLoginLessThanTwo() {
        userUnderTest.setLogin("t");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("login")).isEqualTo(1);
        assertThat(errors.getFieldError("login").getDefaultMessage())
                .isEqualTo("Your length has to be from 2 to 16 symbols");
    }

    @Test
    public void validate_loginError_lengthLoginMoreThan16() {
        userUnderTest.setLogin("timurtimurtimurtimurtimurtimur");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("login")).isEqualTo(1);
        assertThat(errors.getFieldError("login").getDefaultMessage())
                .isEqualTo("Your length has to be from 2 to 16 symbols");
    }

    @Test
    public void validate_passwordError_lengthLoginLessThanSix() {
        userUnderTest.setPassword("123");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
        assertThat(errors.getFieldError("password").getDefaultMessage())
                .isEqualTo("The length of password has to be from 6 to 16");
    }

    @Test
    public void validate_passwordError_lengthLoginMoreThanSixteen() {
        userUnderTest.setPassword("123123123123123123123123123123123123");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
        assertThat(errors.getFieldError("password").getDefaultMessage())
                .isEqualTo("The length of password has to be from 6 to 16");
    }

    @Test
    public void validate_passwordError_passwordNotMatchesRegex() {
        userUnderTest.setPassword("timur2timur2");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
        assertThat(errors.getFieldError("password").getDefaultMessage())
                .isEqualTo("Your password must contain one capital letter one small letter and one number");
    }

    @Test
    public void validate_passwordError_passwordIsWeak() {
        userUnderTest.setPassword("Qwerty1");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
        assertThat(errors.getFieldError("password").getDefaultMessage())
                .startsWith("Your password is to easy. Here is some suggestions to help you:");
    }

    @Test
    public void validate_passwordError_confirmPasswordNotTheSame() {
        userUnderTest.setConfirmPassword("KLJsmajxhjsjs23");
        Errors errors = new BeanPropertyBindingResult(userUnderTest, "validUserDto");
        validator.validate(userUnderTest, errors);
        assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(1);
        assertThat(errors.getFieldError("confirmPassword").getDefaultMessage())
                .isEqualTo("The passwords aren't same. Please enter the same passwords");
    }
}