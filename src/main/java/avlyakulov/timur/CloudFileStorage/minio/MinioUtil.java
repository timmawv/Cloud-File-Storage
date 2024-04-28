package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioClientNotAuthenticatedException;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MinioUtil {

    public static void checkAuthMinio(MinioClient minioClient) {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            log.debug("You were authenticated in minio");
        } catch (MinioException e) {
            log.error("Please check your credentials in Minio");
            throw new MinioClientNotAuthenticatedException("You are not authenticated in Minio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
