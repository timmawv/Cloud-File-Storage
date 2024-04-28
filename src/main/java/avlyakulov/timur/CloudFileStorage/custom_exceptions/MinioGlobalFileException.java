package avlyakulov.timur.CloudFileStorage.custom_exceptions;

public class MinioGlobalFileException extends RuntimeException {
    public MinioGlobalFileException(String message) {
        super(message);
    }
}