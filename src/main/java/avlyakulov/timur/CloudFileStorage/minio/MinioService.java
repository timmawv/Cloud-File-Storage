package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.dto.CreateDirRequest;
import avlyakulov.timur.CloudFileStorage.dto.CreateFileDto;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.dto.UpdateFileNameDto;
import avlyakulov.timur.CloudFileStorage.mapper.FileMapper;
import avlyakulov.timur.CloudFileStorage.util.converter.FileNameConverter;
import avlyakulov.timur.CloudFileStorage.util.converter.FileSizeConverter;
import avlyakulov.timur.CloudFileStorage.util.csv_parser.CsvFileParser;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final FacadeMinioRepository minioRepository;

    private final FileMapper fileMapper;

    public void uploadFile(CreateFileDto createFileDto, Integer userId) {
        minioRepository.uploadFile(createFileDto.getPath(), createFileDto.getFiles(), userId);
    }

    public void uploadEmptyDir(CreateDirRequest createDirRequest, Integer userId) {
        minioRepository.uploadEmptyDir(createDirRequest.getDirName(), createDirRequest.getPath(), userId);
    }

    public List<FileResponse> getUserFiles(String path, Integer userId) {
        List<Item> objectsFromStorage = minioRepository.getAllFilesFromPath(path, userId);
        List<FileResponse> fileResponses = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        //todo this logic is too complicated
        fileResponses.forEach(FileNameConverter::convertFileName);
        fileResponses.forEach(FileSizeConverter::convertFileSize);
        fileResponses.forEach(CsvFileParser::setFileIconForFile);
        return fileResponses;
    }

    public List<FileResponse> searchFiles(String filePrefix, Integer userId) {
        List<Item> userFilesByPrefix = minioRepository.getAllFilesFromUserDirectory(userId);
        List<FileResponse> fileResponses = fileMapper.mapListItemsFromStorageToListFileResponse(userFilesByPrefix);
        fileResponses.forEach(FileNameConverter::convertFileName);
        fileResponses.forEach(FileSizeConverter::convertFileSize);
        fileResponses.forEach(CsvFileParser::setFileIconForFile);
        return fileResponses.stream()
                .filter(f -> f.getObjectName().toLowerCase().contains(filePrefix.toLowerCase()))
                .toList();
    }

    public String removeFile(String filePath, Boolean isDir, Integer userId) {
        minioRepository.removeFile(filePath, isDir, userId);
        return getPathToObjectDirectory(filePath, isDir);
    }

    public InputStream downloadFile(String filePath, Integer userId) {
        return minioRepository.downloadFile(filePath, userId);
    }

    public String updateFileName(UpdateFileNameDto updateFileNameDto, Integer userId) {
        String oldFilePath = updateFileNameDto.getOldFilePath();
        String newFileName = updateFileNameDto.getNewFileName();
        String oldFileName = updateFileNameDto.getOldFileName();
        String pathToFile = "";

        if (oldFilePath.contains("/")) {
            int lastIndexOfSlash = oldFilePath.lastIndexOf("/");
            pathToFile = oldFilePath.substring(0, lastIndexOfSlash + 1);
        }

        if (!updateFileNameDto.getIsFileDirectory()) {
            String newFilePath = "";
            if (oldFileName.contains(".")) {
                int indexOfDot = oldFileName.lastIndexOf(".");
                newFilePath = pathToFile.concat(newFileName).concat(oldFileName.substring(indexOfDot));
            } else {
                newFilePath = pathToFile.concat(newFileName);
            }
            if (newFilePath.equals(oldFilePath)) {
                return pathToFile;
            }
            minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
            removeFile(oldFilePath, updateFileNameDto.getIsFileDirectory(), userId);
            return pathToFile;
        } else {
            //todo make return path to new file directory
            String newPathDir = oldFilePath.replaceFirst("[" + oldFileName + "]+\\/$", newFileName).concat("/");
            minioRepository.copyDirWithNewName(oldFilePath, newPathDir, userId);
            minioRepository.removeFile(oldFilePath, updateFileNameDto.getIsFileDirectory(), userId);
            return newPathDir;
        }
    }

    private String getPathToObjectDirectory(String filePath, Boolean isDir) {
        //todo make more better we can simplify this logic
        if (isDir) {
            long count = filePath.chars().filter(ch -> ch == '/').count();
            if (count == 1)
                return "";
            return filePath.replaceAll("/[^/]+/$", "/");
        }
        if (filePath.contains("/")) {
            int lastIndexOfSlash = filePath.lastIndexOf("/");
            return filePath.substring(0, lastIndexOfSlash + 1);
        }
        return "";
    }
}