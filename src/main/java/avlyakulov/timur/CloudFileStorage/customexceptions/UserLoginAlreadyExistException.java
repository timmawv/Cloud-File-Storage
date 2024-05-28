package avlyakulov.timur.CloudFileStorage.customexceptions;

public class UserLoginAlreadyExistException extends RuntimeException {
    public UserLoginAlreadyExistException(String message) {
        super(message);
    }
}
