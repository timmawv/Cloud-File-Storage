package avlyakulov.timur.CloudFileStorage.service;

import avlyakulov.timur.CloudFileStorage.IntegrationBaseTest;
import avlyakulov.timur.CloudFileStorage.exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.user.User;
import avlyakulov.timur.CloudFileStorage.user.UserDto;
import avlyakulov.timur.CloudFileStorage.user.UserRepository;
import avlyakulov.timur.CloudFileStorage.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = IntegrationBaseTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final UserDto userValid = new UserDto("timur", "KLJsmajxhjsjs2", "KLJsmajxhjsjs2");

    @BeforeAll
    void clearUsers() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void saveUser_userWasSaved_validUser() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(userValid, "validUserDto");
        Model model = new ConcurrentModel();
        userService.saveUser(userValid, bindingResult, model);

        List<User> users = userRepository.findAll();
        User user = users.get(0);

        boolean successRegistration = (boolean) model.getAttribute("success_registration");
        assertThat(successRegistration).isTrue();
        assertThat(user.getLogin()).isEqualTo(userValid.getLogin());
        assertThat(users).hasSize(1);
    }

    @Test
    public void saveUser_userNotSaved_userAlreadyExists() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(userValid, "validUserDto");
        Model model = new ConcurrentModel();
        userService.saveUser(userValid, bindingResult, model);

        userService.saveUser(userValid, bindingResult, model);

        assertThat(bindingResult.hasErrors()).isTrue();
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }
}