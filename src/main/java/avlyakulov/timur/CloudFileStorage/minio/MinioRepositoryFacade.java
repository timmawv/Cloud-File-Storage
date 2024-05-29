package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Repository
public class MinioRepositoryFacade {

    private final MinioRepository minioRepository;

    private final MinioFileRepository minioFileRepository;

    private final MinioDirRepository minioDirRepository;

    public MinioRepositoryFacade(@Qualifier("minioRepository") MinioRepository minioRepository, @Qualifier("minioFileRepository") MinioFileRepository minioFileRepository, @Qualifier("minioDirRepository") MinioDirRepository minioDirRepository) {
        this.minioRepository = minioRepository;
        this.minioFileRepository = minioFileRepository;
        this.minioDirRepository = minioDirRepository;
    }

    public List<Item> getFilesFromPath(String path, Integer userId) {
        return minioRepository.getObjectsFromPath(path, userId);
    }

    public List<Item> getFilesRecursiveFromPath(String path, Integer userId) {
        return minioRepository.getObjectsRecursiveFromPath(path, userId);
    }

    public List<Item> getFilesRecursiveFromUserDirectory(Integer userId) {
        return minioFileRepository.getFilesRecursiveFromUserDirectory(userId);
    }

    public void uploadFile(String pathToFile, MultipartFile[] files, Integer userId) {
        minioRepository.uploadFile(pathToFile, files, userId);
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

    public void copyFileWithNewName(String newFilePath, String oldFilePath, Integer userId) {
        minioFileRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
    }

    public void copyDirWithNewName(String oldFilePath, String newPathDir, Integer userId) {
        minioDirRepository.copyDirWithNewName(oldFilePath, newPathDir, userId);
    }

    public InputStream downloadFile(String filePath, Integer userId) {
        return minioFileRepository.downloadObject(filePath, userId);
    }
}