package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.exceptions.MinioGlobalFileException;
import avlyakulov.timur.CloudFileStorage.util.strings.StringFileUtils;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Repository
public class MinioFileRepository extends MinioRepository {

    public MinioFileRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public void removeFile(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId, filePath);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(usersBucketName).object(userFilePath).build());
        } catch (Exception e) {
            log.error("Error during deleting file " + e.getMessage());
            throw new MinioGlobalFileException("Error during deleting file");
        }
    }

    public List<Item> getFilesRecursiveFromUserDirectory(Integer userId) {
        return getObjectsRecursiveFromPath(StringFileUtils.EMPTY_STRING, userId);
    }

    public void copyFileWithNewName(String newFilePath, String oldFilePath, Integer userId) {
        String newFilePathName = String.format(userDirectory, userId, newFilePath);
        String oldFilePathName = String.format(userDirectory, userId, oldFilePath);
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(newFilePathName)
                            .source(
                                    CopySource.builder()
                                            .bucket(usersBucketName)
                                            .object(oldFilePathName)
                                            .build())
                            .build());
        } catch (Exception e) {
            log.error("Error during copying object " + e.getMessage());
            throw new MinioGlobalFileException("Error during copying file or dir");
        }
    }

    public InputStream downloadObject(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId, filePath);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(userFilePath)
                            .build());
        } catch (Exception e) {
            log.error("Error during downloading object from storage " + e.getMessage());
            throw new MinioGlobalFileException("Error during downloading file from server");
        }
    }
}