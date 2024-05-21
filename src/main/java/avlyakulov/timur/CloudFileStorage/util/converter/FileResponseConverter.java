package avlyakulov.timur.CloudFileStorage.util.converter;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.util.csv_parser.CsvFileParser;

import java.util.List;

public class FileResponseConverter {

    public static void convertFieldsFileResponse(List<FileResponse> fileList) {
        fileList.forEach(FileResponseConverter::convertObjectName);
        fileList.forEach(FileResponseConverter::convertObjectPath);
        fileList.forEach(FileSizeConverter::convertFileSize);
        fileList.forEach(CsvFileParser::setFileIconForFile);
    }

    private static void convertObjectName(FileResponse fileResponse) {
        if (fileResponse.getIsDirectory())
            convertDirName(fileResponse);
        else
            convertFileName(fileResponse);
    }

    private static void convertObjectPath(FileResponse fileResponse) {
        String filePath = fileResponse.getFilePath();
        int indexOfFirstSlash = filePath.indexOf("/");
        fileResponse.setFilePath(filePath.substring(indexOfFirstSlash + 1));
    }

    private static void convertFileName(FileResponse fileResponse) {
        String filePath = fileResponse.getFilePath();
        int indexOfLastSlash = filePath.lastIndexOf("/");
        fileResponse.setObjectName(filePath.substring(indexOfLastSlash + 1));
    }

    private static void convertDirName(FileResponse fileResponse) {
        String filePath = fileResponse.getFilePath();
        String[] filePathSplit = filePath.split("/");
        fileResponse.setObjectName(filePathSplit[filePathSplit.length - 1]);
    }

}