package avlyakulov.timur.CloudFileStorage.service;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.dto.UserDto;
import avlyakulov.timur.CloudFileStorage.mapper.UserMapper;
import avlyakulov.timur.CloudFileStorage.model.User;
import avlyakulov.timur.CloudFileStorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

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
