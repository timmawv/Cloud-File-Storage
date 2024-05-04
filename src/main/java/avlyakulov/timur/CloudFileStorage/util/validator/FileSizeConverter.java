package avlyakulov.timur.CloudFileStorage.util.validator;

import java.text.DecimalFormat;

public class FileSizeConverter {

    private static final double unit = 1024.0;

    public static String convertFileSize(String fileSize) {
        DecimalFormat df = new DecimalFormat("#.##");
        int fileSizeInt = Integer.parseInt(fileSize);
        double fileSizeKB = fileSizeInt / unit;
        if (fileSizeKB <= unit)
            return df.format(fileSizeKB).concat(" KB");
        double fileSizeMB = fileSizeKB / unit;
        if (fileSizeMB <= unit)
            return df.format(fileSizeMB).concat(" MB");;
        return df.format(fileSizeMB / unit).concat(" GB");
    }
}
