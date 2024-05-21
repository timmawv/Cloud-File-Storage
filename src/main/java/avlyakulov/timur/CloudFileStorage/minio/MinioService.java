package avlyakulov.timur.CloudFileStorage.minio;

import avlyakulov.timur.CloudFileStorage.dto.CreateDirRequest;
import avlyakulov.timur.CloudFileStorage.dto.CreateFileDto;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.dto.UpdateFileNameDto;
import avlyakulov.timur.CloudFileStorage.mapper.FileMapper;
import avlyakulov.timur.CloudFileStorage.util.converter.FileResponseConverter;
import avlyakulov.timur.CloudFileStorage.util.strings.StringFileUtils;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

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
        List<FileResponse> filesResponse = fileMapper.mapListItemsFromStorageToListFileResponse(objectsFromStorage);
        FileResponseConverter.convertFieldsFileResponse(filesResponse);
        return filesResponse;
    }

    public List<FileResponse> searchFiles(String filePrefix, Integer userId) {
        List<Item> userFilesByPrefix = minioRepository.getAllFilesFromUserDirectory(userId);
        List<FileResponse> filesResponse = fileMapper.mapListItemsFromStorageToListFileResponse(userFilesByPrefix);
        FileResponseConverter.convertFieldsFileResponse(filesResponse);
        return filesResponse.stream()
                .filter(f -> f.getObjectName().toLowerCase().contains(filePrefix.toLowerCase()))
                .toList();
    }

    public String removeFile(String filePath, Boolean isDir, Integer userId) {
        minioRepository.removeFile(filePath, isDir, userId);
        return StringFileUtils.getPathToObjectDirectory(filePath, isDir);
    }

    public InputStream downloadFile(String filePath, Integer userId) {
        return minioRepository.downloadFile(filePath, userId);
    }

    public String updateFileName(UpdateFileNameDto updateFileNameDto, Integer userId) {
        String oldFileName = updateFileNameDto.getOldFileName();
        String oldFilePath = updateFileNameDto.getOldFilePath();
        String newFileName = updateFileNameDto.getNewFileName();

        String pathToFile = StringFileUtils.getPathToObjectDirectory(oldFilePath, updateFileNameDto.getIsFileDirectory());

        if (!updateFileNameDto.getIsFileDirectory()) {
            String newFilePath = pathToFile.concat(newFileName).concat(StringFileUtils.getFileType(oldFileName));
            if (newFilePath.equals(oldFilePath)) {
                return pathToFile;
            }
            minioRepository.copyFileWithNewName(newFilePath, oldFilePath, userId);
            removeFile(oldFilePath, updateFileNameDto.getIsFileDirectory(), userId);
        } else {
            String escapedFileName = Pattern.quote(oldFileName);
            String newPathDir = oldFilePath.replaceFirst("[" + escapedFileName + "]+\\/$", newFileName).concat("/");
            minioRepository.copyDirWithNewName(oldFilePath, newPathDir, userId);
            minioRepository.removeFile(oldFilePath, updateFileNameDto.getIsFileDirectory(), userId);
        }
        return pathToFile;
    }
}