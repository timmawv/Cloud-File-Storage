package avlyakulov.timur.CloudFileStorage.service;

import avlyakulov.timur.CloudFileStorage.IntegrationTestBase;
import avlyakulov.timur.CloudFileStorage.custom_exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.dto.UserDto;
import avlyakulov.timur.CloudFileStorage.model.User;
import avlyakulov.timur.CloudFileStorage.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final UserDto userValid = new UserDto("timur", "KLJsmajxhjsjs2", "KLJsmajxhjsjs2");

    @AfterEach
    void tearUsers() {
        userRepository.deleteAll();
    }


    @Test
    public void saveUser_userWasSaved_validUser() {
        userService.saveUser(userValid);

        List<User> users = userRepository.findAll();
        User user = users.get(0);

        assertThat(user.getLogin()).isEqualTo(userValid.getLogin());
        assertThat(users).hasSize(1);
    }

    @Test
    public void saveUser_userNotSaved_userAlreadyExists() {
        userService.saveUser(userValid);

        UserLoginAlreadyExistException userLoginAlreadyExistException = assertThrows(UserLoginAlreadyExistException.class, () -> userService.saveUser(userValid));
        assertThat(userLoginAlreadyExistException.getMessage()).isEqualTo("User with such login already exists");

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }
}