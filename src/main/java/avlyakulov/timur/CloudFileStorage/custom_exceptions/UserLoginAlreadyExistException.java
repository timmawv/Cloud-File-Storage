package avlyakulov.timur.CloudFileStorage.custom_exceptions;

public class UserLoginAlreadyExistException extends RuntimeException {
    public UserLoginAlreadyExistException(String message) {
        super(message);
    }
}
