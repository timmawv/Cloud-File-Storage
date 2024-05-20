package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.MinioClient;
import io.minio.messages.Item;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Primary
@Repository

public class FacadeMinioRepository extends MinioRepository {

    private final MinioFileRepository minioFileRepository;

    private final MinioDirRepository minioDirRepository;

    public FacadeMinioRepository(MinioClient minioClient, MinioFileRepository minioFileRepository, MinioDirRepository minioDirRepository) {
        super(minioClient);
        this.minioFileRepository = minioFileRepository;
        this.minioDirRepository = minioDirRepository;
    }

    public List<Item> getAllFilesFromPath(String path, Integer userId) {
        return super.getObjectsFromPath(path, userId);
    }

    public List<Item> getAllFilesFromUserDirectory(Integer userId) {
        return minioFileRepository.getAllFilesFromUserDirectory(userId);
    }

    public void uploadFile(String pathToFile, MultipartFile[] files, Integer userId) {
        super.uploadFile(pathToFile, files, userId);
    }

    public void uploadEmptyDir(String dirName, String pathToDir, Integer userId) {
        minioDirRepository.createEmptyDirectory(dirName, pathToDir, userId);
    }

    public void removeFile(String filePath, Boolean isDir, Integer userId) {
        if (isDir)
            minioDirRepository.removeDirectory(filePath, userId);
        else
            minioFileRepository.removeFile(filePath, userId);
    }

    public void copyFileWithNewName(String pathNewFile, String pathOldFile, Integer userId) {
        minioFileRepository.copyFileWithNewName(pathNewFile, pathOldFile, userId);
    }

    public void copyDirWithNewName(String oldFilePath, String newPathDir, Integer userId) {
        minioDirRepository.copyDirWithNewName(oldFilePath, newPathDir, userId);
    }

    public InputStream downloadFile(String filePath, Integer userId) {
        return minioFileRepository.downloadObject(filePath, userId);
    }
}