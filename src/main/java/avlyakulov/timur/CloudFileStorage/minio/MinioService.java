package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MinioService {

    private MinioClient minioClient;

    private String userDirectory = "user-%d-files";

    private String usersBucketName = "user-files";

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createUserDirectory(Integer userId) {
        MinioUtil.checkAuthMinio(minioClient);
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(usersBucketName).build());
        } catch (MinioException e) {

        } catch (Exception e) {

        }
    }
}