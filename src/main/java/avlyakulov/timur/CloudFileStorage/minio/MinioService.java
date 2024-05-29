package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.exceptions.SearchQueryException;
import avlyakulov.timur.CloudFileStorage.minio.dto.DirRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRequest;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.minio.dto.FileRenameRequest;
import avlyakulov.timur.CloudFileStorage.mapper.FileMapper;
import avlyakulov.timur.CloudFileStorage.user.UserRepository;
import avlyakulov.timur.CloudFileStorage.util.CountFilesSize;
import avlyakulov.timur.CloudFileStorage.util.converter.FileResponseConverter;
import avlyakulov.timur.CloudFileStorage.util.converter.FileSizeConverter;
import avlyakulov.timur.CloudFileStorage.util.strings.StringFileUtils;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioRepositoryFacade minioRepository;

    private final UserRepository userRepository;

    private final FileMapper fileMapper;

    public void uploadFile(FileRequest fileRequest, Integer userId) {
        minioRepository.uploadFile(fileRequest.getPath(), fileRequest.getFiles(), userId);
        BigInteger sizeOfFiles = CountFilesSize.countFileSize(Arrays.asList(fileRequest.getFiles()));
        userRepository.increaseUserCapacity(sizeOfFiles, userId);
    }

    public BigDecimal getUserCapacity(Integer userId) {
        return FileSizeConverter.convertBytesToMB(userRepository.findUserCapacity(userId).orElse(BigInteger.ZERO));
    }

    public void uploadEmptyDir(DirRequest dirRequest, Integer userId) {
        minioRepository.uploadEmptyDir(dirRequest.getDirName(), dirRequest.getPath(), userId);
    }

    public List<FileResponse> getUserFiles(String path, Integer userId) {
        List<Item> objectsFromStorage = minioRepository.getFilesFromPath(path, userId);
        List<FileResponse> filesResponse = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        FileResponseConverter.convertFieldsFileResponse(filesResponse);
        return filesResponse;
    }

    public List<FileResponse> searchFiles(String filePrefix, Integer userId) {
        if (StringUtils.isBlank(filePrefix))
            throw new SearchQueryException("You search query for file is wrong");
        List<Item> userFilesByPrefix = minioRepository.getFilesRecursiveFromUserDirectory(userId);
        List<FileResponse> filesResponse = fileMapper.mapListItemsFromStorageToListFileResponse(userFilesByPrefix);
        FileResponseConverter.convertFieldsFileResponse(filesResponse);
        return filesResponse.stream()
                .filter(f -> f.getObjectName().toLowerCase().contains(filePrefix.toLowerCase()))
                .toList();
    }

    public String removeFile(String filePath, Boolean isDir, Integer userId) {
        List<Item> files = minioRepository.getFilesRecursiveFromPath(filePath, userId);
        minioRepository.removeFile(filePath, isDir, userId);
        BigInteger sizeOfFile = CountFilesSize.countItemSize(files);
        userRepository.decreaseUserCapacity(sizeOfFile, userId);
        return StringFileUtils.getPathToObjectDirectory(filePath, isDir);
    }

    public InputStream downloadFile(String filePath, Integer userId) {
        return minioRepository.downloadFile(filePath, userId);
    }

    public String updateFileName(FileRenameRequest fileRenameRequest, Integer userId) {
        String oldFileName = fileRenameRequest.getOldFileName();
        String oldFilePath = fileRenameRequest.getOldFilePath();
        String newFileName = fileRenameRequest.getNewFileName();

        String pathToFile = StringFileUtils.getPathToObjectDirectory(oldFilePath, fileRenameRequest.getIsFileDirectory());

        if (!fileRenameRequest.getIsFileDirectory()) {
            String newFilePath = pathToFile.concat(newFileName).concat(StringFileUtils.getFileType(oldFileName));
            if (newFilePath.equals(oldFilePath)) {
                return pathToFile;
            }
            minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
            minioRepository.removeFile(oldFilePath, fileRenameRequest.getIsFileDirectory(), userId);
        } else {
            String escapedFileName = Pattern.quote(oldFileName);
            String newPathDir = oldFilePath.replaceFirst("[" + escapedFileName + "]+\\/$", newFileName).concat("/");
            minioRepository.copyDirWithNewName(oldFilePath, newPathDir, userId);
            minioRepository.removeFile(oldFilePath, fileRenameRequest.getIsFileDirectory(), userId);
        }
        return pathToFile;
    }
}