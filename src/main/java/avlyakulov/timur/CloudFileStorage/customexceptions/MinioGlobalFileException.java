package avlyakulov.timur.CloudFileStorage.customexceptions;

public class MinioGlobalFileException extends RuntimeException {
    public MinioGlobalFileException(String message) {
        super(message);
    }
}