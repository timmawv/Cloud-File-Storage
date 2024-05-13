package avlyakulov.timur.CloudFileStorage.util.converter;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;

import java.text.DecimalFormat;

public class FileSizeConverter {

    private static final double unit = 1024.0;

    public static void convertFileSize(FileResponse fileResponse) {
        DecimalFormat df = new DecimalFormat("#.##");
        int fileSizeInt = Integer.parseInt(fileResponse.getSize());
        double fileSizeKB = fileSizeInt / unit;
        if (fileSizeKB <= unit) {
            fileResponse.setSize(df.format(fileSizeKB).concat(" KB"));
            return;
        }
        double fileSizeMB = fileSizeKB / unit;
        if (fileSizeMB <= unit) {
            fileResponse.setSize(df.format(fileSizeMB).concat(" MB"));
            return;
        }
        fileResponse.setSize(df.format(fileSizeMB / unit).concat(" GB"));
    }
}