package avlyakulov.timur.CloudFileStorage.service;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.UserLoginAlreadyExistException;
import avlyakulov.timur.CloudFileStorage.dto.UserDto;
import avlyakulov.timur.CloudFileStorage.mapper.UserMapper;
import avlyakulov.timur.CloudFileStorage.model.User;
import avlyakulov.timur.CloudFileStorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

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

    @Transactional
    public void increaseUserCapacity(BigInteger capacity, Integer userId) {
        userRepository.increaseUserCapacity(capacity, userId);
    }

    @Transactional
    public void decreaseUserCapacity(BigInteger capacity, Integer userId) {
        userRepository.decreaseUserCapacity(capacity, userId);
    }
}