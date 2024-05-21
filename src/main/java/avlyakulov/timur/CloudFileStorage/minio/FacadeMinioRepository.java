package avlyakulov.timur.CloudFileStorage.minio;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FacadeMinioRepository {

    private final MinioRepository minioRepository;

    private final MinioFileRepository minioFileRepository;

    private final MinioDirRepository minioDirRepository;


    public List<Item> getAllFilesFromPath(String path, Integer userId) {
        return minioRepository.getObjectsFromPath(path, userId);
    }

    public List<Item> getAllFilesFromUserDirectory(Integer userId) {
        return minioFileRepository.getAllFilesFromUserDirectory(userId);
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