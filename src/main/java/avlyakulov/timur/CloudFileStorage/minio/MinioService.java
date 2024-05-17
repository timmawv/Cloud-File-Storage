package avlyakulov.timur.CloudFileStorage.minio;

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
import org.springframework.core.io.InputStreamResource;
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

    public List<FileResponse> getUserFiles(String path, Integer userId) {
        List<Item> objectsFromStorage = minioRepository.getAllFilesFromPath(path, userId);
        List<FileResponse> fileResponses = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        fileResponses.forEach(FileNameConverter::convertFileName);
        fileResponses.forEach(FileSizeConverter::convertFileSize);
        fileResponses.forEach(CsvFileParser::setFileIconForFile);
        return fileResponses;
    }

    public String removeFile(String filePath, Boolean isDir, Integer userId) {
        minioRepository.removeFile(filePath, isDir, userId);
        return getPathToObjectsDirectory(filePath, isDir);
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
        }
        //todo make implementation for folder
        String newFilePath = oldFilePath.replace(oldFileName, newFileName);
        minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
        return pathToFile;
    }

    private String getPathToObjectsDirectory(String filePath, Boolean isDir) {
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