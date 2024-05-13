package avlyakulov.timur.CloudFileStorage.minio;

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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioUtil minioUtil;

    private final FileMapper fileMapper;

    public void uploadFile(MultipartFile[] files, Integer userId) {
        minioUtil.uploadFile(files, userId);
    }

    public List<FileResponse> getUserFiles(Integer userId, String path) {
        List<Item> objectsFromStorage = minioUtil.getObjectsFromPath(userId, path);
        List<FileResponse> fileResponses = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        fileResponses.forEach(FileNameConverter::convertFileName);
        fileResponses.forEach(FileSizeConverter::convertFileSize);
        fileResponses.forEach(CsvFileParser::setFileIconForFile);
        return fileResponses;
    }

    public String deleteFile(String filePath, Integer userId) {
        minioUtil.deleteFile(filePath, userId);
        return getPathToFileDirectory(filePath);
    }


    public String updateFileName(UpdateFileNameDto updateFileNameDto, Integer userId) {
        String oldFilePath = updateFileNameDto.getOldFilePath();
        String newFileName = updateFileNameDto.getNewFileName();
        String oldFileName = updateFileNameDto.getOldFileName();
        String pathToFile = "";

        if (oldFilePath.contains("/")) {
            int lastIndexOfSlash = oldFilePath.lastIndexOf("/");
            pathToFile = oldFilePath.substring(0, lastIndexOfSlash);
        }

        if (!updateFileNameDto.getIsFileDirectory()) {
            int indexOfDot = oldFileName.lastIndexOf(".");
            String newFilePath = oldFilePath.replace(oldFileName.substring(0, indexOfDot), newFileName);
            minioUtil.copyFileWithNewName(newFilePath, oldFilePath, userId);
            deleteFile(oldFilePath, userId);
            return pathToFile;
        }
        String newFilePath = oldFilePath.replace(oldFileName, newFileName);
        minioUtil.copyFileWithNewName(newFilePath, oldFilePath, userId);
        return pathToFile;
    }

    private String getPathToFileDirectory(String filePath) {
        if (filePath.contains("/")) {
            int lastIndexOfSlash = filePath.lastIndexOf("/");
            return filePath.substring(0, lastIndexOfSlash + 1);
        }
        return "";
    }
}