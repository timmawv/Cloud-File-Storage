package avlyakulov.timur.CloudFileStorage.exceptions;

public class MinioClientNotAuthenticatedException extends RuntimeException {
    public MinioClientNotAuthenticatedException(String message) {
        super(message);
    }
}