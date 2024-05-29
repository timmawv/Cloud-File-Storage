package avlyakulov.timur.CloudFileStorage.user;

import avlyakulov.timur.CloudFileStorage.exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveUser(UserDto userDto) {
        User user = userMapper.mapUserDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserLoginAlreadyExistException("User with such login already exists");
        }
    }
}