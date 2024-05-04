package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioGlobalFileException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public void uploadFile(MultipartFile file, Integer userId) {
        MinioUtil.checkAuthMinio(minioClient);
        createMainBucketIfItNotExist();
        String userDirectoryFormatted = String.format(userDirectory, userId).concat("/".concat(file.getOriginalFilename()));
        createFileWithUserDirectory(file, userDirectoryFormatted);
    }

    public Iterable<Result<Item>> getObjectsFromStorage(Integer userId, String path) {
        String userDirectoryFormatted = String.format(userDirectory, userId).concat(path);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(usersBucketName)
                        .prefix(userDirectoryFormatted)
                        .build());
        return results;
    }

    //todo refactor it to minioUtil
    private void createMainBucketIfItNotExist() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(usersBucketName).build());
            if (!found)
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(usersBucketName).build());
        } catch (Exception e) {
            log.error("something went wrong with minio while was creating main bucket");
            throw new MinioGlobalFileException("Something went wrong with minio");
        }
    }

    private void createFileWithUserDirectory(MultipartFile file, String userDirectory) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(userDirectory)
                            .stream(file.getInputStream(), -1, 10485760)
                            .build());

        } catch (Exception e) {
            log.error("something went wrong with minio while was creating user directory");
        }
    }
}