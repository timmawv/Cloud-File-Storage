package avlyakulov.timur.CloudFileStorage.exceptions;

public class UserLoginAlreadyExistException extends RuntimeException {
    public UserLoginAlreadyExistException(String message) {
        super(message);
    }
}
