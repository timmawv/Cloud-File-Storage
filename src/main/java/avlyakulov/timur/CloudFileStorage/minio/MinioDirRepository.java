package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Repository
public class MinioDirRepository extends MinioRepository {

    public MinioDirRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public void createEmptyDirectory(String dirName, String pathToDir, Integer userId) {
        String userDirectoryPath = String.format(userDirectory, userId).concat(pathToDir).concat(dirName.concat("/"));
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(usersBucketName).object(userDirectoryPath)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error("Error during creating empty directory");
        }
    }

    public void removeDirectory(String filePath, Integer userId) {
        String userFilePath = String.format(userDirectory, userId).concat(filePath);
        removeAllFilesInDirectory(userFilePath);
    }

    private void removeAllFilesInDirectory(String path) {
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
}