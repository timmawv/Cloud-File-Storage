package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.custom_exceptions.MinioGlobalFileException;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Repository
public class MinioDirRepository extends MinioRepository {

    public MinioDirRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public void createEmptyDirectory(String dirName, String pathToDir, Integer userId) {
        String userDirectoryPath = String.format(userDirectory, userId, pathToDir.concat(dirName.concat("/")));
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(usersBucketName).object(userDirectoryPath)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error("Error during creating empty directory");
            throw new MinioGlobalFileException("Error during creating empty dir on the server");
        }
    }

    public void copyDirWithNewName(String oldFilePath, String newDirPath, Integer userId) {
        List<Item> filesInDir = getObjectsRecursiveFromPath(oldFilePath, userId);
        for (Item item : filesInDir) {
            String fullFilePath = item.objectName();
            String newFilePath = fullFilePath.replace(oldFilePath, newDirPath);
            copyFileWithNewName(fullFilePath, newFilePath);
        }
    }

    //removing all files in dir to delete
    public void removeDirectory(String filePath, Integer userId) {
        List<Item> files = getObjectsRecursiveFromPath(filePath, userId);
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
                throw new MinioGlobalFileException("Error during removing directory");
            }
        }
    }

    private void copyFileWithNewName(String oldPath, String newPath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(usersBucketName)
                            .object(newPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(usersBucketName)
                                            .object(oldPath)
                                            .build())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MinioGlobalFileException("Error during copying file with new name");
        }
    }
}