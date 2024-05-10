package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioClientNotAuthenticatedException;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinioUtil {

    public static void checkAuthMinio(MinioClient minioClient) {
        try {
            minioClient.listBuckets();
            log.debug("You were authenticated in minio");
        } catch (MinioException e) {
            log.error("Please check your credentials in Minio");
            throw new MinioClientNotAuthenticatedException("Something went wrong with file storage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}