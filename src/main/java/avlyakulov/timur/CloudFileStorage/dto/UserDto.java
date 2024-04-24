package avlyakulov.timur.CloudFileStorage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull(message = "Your login can't be null")
    private String login;

    @NotNull(message = "Your password can't be null")
    private String password;

    @NotNull(message = "Your confirm password can't be null")
    private String confirmPassword;
}
