package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MinioFileRepository extends MinioRepository {

    public MinioFileRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public void removeFile(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId).concat(filePath);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(usersBucketName).object(userFilePath).build());
        } catch (Exception e) {
            log.error("Error during deleting file");
        }
    }

    public List<Item> getAllFilesFromUserDirectory(Integer userId) {
        String userBaseDirectory = String.format(userDirectory, userId);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(usersBucketName)
                        .prefix(userBaseDirectory)
                        .recursive(true)
                        .build());
        List<Item> filesInDir = new ArrayList<>();
        for (Result<Item> item : results) {
            try {
                filesInDir.add(item.get());
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