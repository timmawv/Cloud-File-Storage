package avlyakulov.timur.CloudFileStorage.repository;

import avlyakulov.timur.CloudFileStorage.UserIntegrationTestBase;
import avlyakulov.timur.CloudFileStorage.user.User;
import avlyakulov.timur.CloudFileStorage.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends UserIntegrationTestBase {

    private User user = new User("timur", "123");

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearUsers() {
        userRepository.deleteAll();
        user = new User("timur", "123");
    }

    @Test
    void findByLogin_wasFound() {
        userRepository.save(user);

        Optional<User> maybeUserOptional = userRepository.findByLogin(user.getLogin());

        assertThat(maybeUserOptional).isPresent();
        User maybeUser = maybeUserOptional.get();
        assertThat(maybeUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(maybeUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void findByLogin_notFound() {
        Optional<User> maybeUserOptional = userRepository.findByLogin(user.getLogin());

        assertThat(maybeUserOptional).isEmpty();
    }
}