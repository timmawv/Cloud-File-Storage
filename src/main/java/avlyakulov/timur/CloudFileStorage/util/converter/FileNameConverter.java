package avlyakulov.timur.CloudFileStorage.util.converter;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;

public class FileNameConverter {

    public static void convertFileName(FileResponse fileResponse) {
        if (fileResponse.isDirectory()) {
            convertDirectory(fileResponse);
            return;
        }
        convertFile(fileResponse);
    }

    private static void convertFile(FileResponse fileResponse) {
        String filePath = fileResponse.getFilePath();
        int firstSlash = filePath.indexOf("/");
        int lastSlash = filePath.lastIndexOf("/");
        fileResponse.setObjectName(filePath.substring(lastSlash + 1));
        fileResponse.setFilePath(filePath.substring(firstSlash + 1));
    }

    private static void convertDirectory(FileResponse fileResponse) {
        String filePath = fileResponse.getFilePath();
        int firstSlash = filePath.indexOf("/");
        String[] filePathSplit = filePath.split("/");
        fileResponse.setObjectName(filePathSplit[filePathSplit.length - 1]);
        fileResponse.setFilePath(filePath.substring(firstSlash + 1));
    }
}