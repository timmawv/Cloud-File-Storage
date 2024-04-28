package avlyakulov.timur.CloudFileStorage.custom_exceptions;

public class MinioClientNotAuthenticatedException extends RuntimeException {
    public MinioClientNotAuthenticatedException(String message) {
        super(message);
    }
}