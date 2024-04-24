package avlyakulov.timur.CloudFileStorage.config.security;

import avlyakulov.timur.CloudFileStorage.model.User;
import avlyakulov.timur.CloudFileStorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class PersonDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(username);

        return user.map(PersonDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with such login or password doesn't exist"));
    }
}
