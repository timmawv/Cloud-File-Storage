package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioClientNotAuthenticatedException;
import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioGlobalFileException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;
    private final String userDirectory = "user-%d-files/";
    private final String usersBucketName = "user-files";

    public List<Item> getObjectsFromPath(Integer userId, String path) {
        baseMinioConfiguration();
        String userDirectoryFormatted = String.format(userDirectory, userId).concat(path);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(usersBucketName)
                        .prefix(userDirectoryFormatted)
                        .build());
        List<Item> filesInDir = new ArrayList<>();
        for (Result<Item> item : results) {
            try {
                filesInDir.add(item.get());
            } catch (Exception e) {
                log.error("Error during adding file to list in minio util");
            }
        }
        return filesInDir;
    }

    public void uploadFile(String pathToFile, MultipartFile[] files, Integer userId) {
        for (MultipartFile file : files) {
            String userDirectoryFormatted = String.format(userDirectory, userId).concat(pathToFile.concat(file.getOriginalFilename()));
            createFile(file, userDirectoryFormatted);
        }
    }

    public void deleteFile(String filePath, Boolean isDir, Integer userId) {
        String userFilePath = String.format(userDirectory, userId).concat(filePath);
        if (isDir)
            removeDirectory(userFilePath);
        else
            removeFile(userFilePath);
    }

    private void removeFile(String userFilePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(usersBucketName).object(userFilePath).build());
        } catch (Exception e) {
            log.error("Error during deleting file");
        }
    }

    private void removeDirectory(String path) {
        List<Item> files = getObjectsFromPathForDeletingDirectory(path);
        List<DeleteObject> objects = new LinkedList<>();
        files.forEach(f -> objects.add(new DeleteObject(f.objectName())));
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(usersBucketName).objects(objects).build());
        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                System.out.println("Error in deleting object " + error.objectName() + "; " + error.message());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Item> getObjectsFromPathForDeletingDirectory(String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket("user-files")
                        .prefix(path)
                        .build());
        List<Item> filesInDir = new ArrayList<>();
        for (Result<Item> item : results) {
            try {
                if (item.get().isDir()) {
                    List<Item> objectsFromPath = getObjectsFromPathForDeletingDirectory(item.get().objectName());
                    filesInDir.addAll(objectsFromPath);
                } else {
                    filesInDir.add(item.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filesInDir;
    }

    public void copyFileWithNewName(String pathNewFileName, String pathOldFileName, Integer userId) {
        String updateFileName = String.format(userDirectory, userId).concat(pathNewFileName);
        String oldFileName = String.format(userDirectory, userId).concat(pathOldFileName);
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(updateFileName)
                            .source(
                                    CopySource.builder()
                                            .bucket(usersBucketName)
                                            .object(oldFileName)
                                            .build())
                            .build());
        } catch (Exception e) {
            log.error("Error during copying object");
            e.printStackTrace();
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
        }

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