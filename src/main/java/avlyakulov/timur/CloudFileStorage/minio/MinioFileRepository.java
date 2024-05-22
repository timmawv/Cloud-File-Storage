package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.util.CountFilesSize;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Repository
public class MinioFileRepository extends MinioRepository {

    public MinioFileRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public BigInteger removeFile(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId).concat(filePath);
        List<Item> files = getObjectsFromPath(filePath, userId);
        BigInteger sizeOfFile = CountFilesSize.countItemSize(files.stream());
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(usersBucketName).object(userFilePath).build());
        } catch (Exception e) {
            log.error("Error during deleting file");
        }
        return sizeOfFile;
    }

    public List<Item> getAllFilesFromUserDirectory(Integer userId) {
        return getObjectsRecursiveFromPath("", userId);
    }

    public void copyFileWithNewName(String newFilePath, String oldFilePath, Integer userId) {
        String newFilePathName = String.format(userDirectory, userId).concat(newFilePath);
        String oldFilePathName = String.format(userDirectory, userId).concat(oldFilePath);
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
            log.error("Error during copying object");
            e.printStackTrace();
        }
    }

    public InputStream downloadObject(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId).concat(filePath);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(userFilePath)
                            .build());
        } catch (Exception e) {
            log.error("Error during downloading object from storage");
            throw new RuntimeException(e);
        }
    }
}