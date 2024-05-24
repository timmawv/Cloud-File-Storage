package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioClientNotAuthenticatedException;
import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioGlobalFileException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MinioRepository {

    protected final MinioClient minioClient;
    protected final String userDirectory = "user-%d-files/%s";
    protected final String usersBucketName = "user-files";

    public List<Item> getObjectsFromPath(String path, Integer userId) {
        baseMinioConfiguration();
        String userDirectoryFormatted = String.format(userDirectory, userId, path);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(usersBucketName)
                        .prefix(userDirectoryFormatted)
                        .build());
        return getItemsFromResult(results);
    }

    public List<Item> getObjectsRecursiveFromPath(String path, Integer userId) {
        String userDirectoryFormatted = String.format(userDirectory, userId, path);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(usersBucketName)
                        .prefix(userDirectoryFormatted)
                        .recursive(true)
                        .build());
        return getItemsFromResult(results);
    }

    public void uploadFile(String pathToFile, MultipartFile[] files, Integer userId) {
        for (MultipartFile file : files) {
            String userDirectoryFormatted = String.format(userDirectory, userId, pathToFile.concat(file.getOriginalFilename()));
            createFile(file, userDirectoryFormatted);
        }
    }

    private void createFile(MultipartFile file, String userDirectory) {
        try {
            ObjectWriteResponse object = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(userDirectory)
                            .stream(file.getInputStream(), -1, 10485760)
                            .build());
            object.object();
        } catch (Exception e) {
            log.error("something went wrong with minio while was creating user directory");
            throw new MinioGlobalFileException("File with such name not supported: " + file.getOriginalFilename());
        }
    }

    public List<Item> getItemsFromResult(Iterable<Result<Item>> results) {
        List<Item> filesInDir = new ArrayList<>();
        for (Result<Item> item : results) {
            try {
                filesInDir.add(item.get());
            } catch (Exception e) {
                log.error("Error during adding file to list in minio util");
                throw new MinioGlobalFileException("Error during load files to page");
            }
        }
        return filesInDir;
    }

    private void baseMinioConfiguration() {
        checkAuthMinio();
        createMainBucketIfItNotExist();
    }

    private void checkAuthMinio() {
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
}