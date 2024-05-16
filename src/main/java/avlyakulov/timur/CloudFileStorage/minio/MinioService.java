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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioRepository minioRepository;

    private final FileMapper fileMapper;

    public void uploadFile(CreateFileDto createFileDto, Integer userId) {
        MultipartFile[] files = createFileDto.getFiles();
        String path = createFileDto.getPath();
        minioRepository.uploadFile(path, files, userId);
    }

    public List<FileResponse> getUserFiles(Integer userId, String path) {
        List<Item> objectsFromStorage = minioRepository.getObjectsFromPath(userId, path);
        List<FileResponse> fileResponses = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        fileResponses.forEach(FileNameConverter::convertFileName);
        fileResponses.forEach(FileSizeConverter::convertFileSize);
        fileResponses.forEach(CsvFileParser::setFileIconForFile);
        return fileResponses;
    }

    public String deleteFile(String filePath, Boolean isDir, Integer userId) {
        minioRepository.deleteFile(filePath, isDir, userId);
        return getPathToFileDirectory(filePath, isDir);
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
            minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
            deleteFile(oldFilePath, updateFileNameDto.getIsFileDirectory(), userId);
            return pathToFile;
        }
        String newFilePath = oldFilePath.replace(oldFileName, newFileName);
        minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
        return pathToFile;
    }

    private String getPathToFileDirectory(String filePath, Boolean isDir) {
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