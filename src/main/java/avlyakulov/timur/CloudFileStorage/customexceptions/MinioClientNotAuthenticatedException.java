package avlyakulov.timur.CloudFileStorage.customexceptions;

public class MinioClientNotAuthenticatedException extends RuntimeException {
    public MinioClientNotAuthenticatedException(String message) {
        super(message);
    }
}