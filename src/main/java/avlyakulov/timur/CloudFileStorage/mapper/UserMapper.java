package avlyakulov.timur.CloudFileStorage.mapper;

import avlyakulov.timur.CloudFileStorage.user.UserDto;
import avlyakulov.timur.CloudFileStorage.user.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    User mapUserDtoToUser(UserDto userDto);
}