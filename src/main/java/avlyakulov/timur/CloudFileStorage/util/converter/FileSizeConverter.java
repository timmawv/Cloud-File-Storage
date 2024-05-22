package avlyakulov.timur.CloudFileStorage.util.converter;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileSizeConverter {

    private static final BigDecimal unit = BigDecimal.valueOf(1024 * 1024);
    private static final double unitDouble = 1024.0;

    public static void convertFileSize(FileResponse fileResponse) {
        DecimalFormat df = new DecimalFormat("#.##");
        int fileSizeInt = Integer.parseInt(fileResponse.getSize());
        double fileSizeKB = fileSizeInt / unitDouble;
        if (fileSizeKB <= unitDouble) {
            fileResponse.setSize(df.format(fileSizeKB).concat(" KB"));
            return;
        }
        double fileSizeMB = fileSizeKB / unitDouble;
        if (fileSizeMB <= unitDouble) {
            fileResponse.setSize(df.format(fileSizeMB).concat(" MB"));
            return;
        }
        fileResponse.setSize(df.format(fileSizeMB / unitDouble).concat(" GB"));
    }


    //todo get rid of rounding cause i need like 0,072 mb etc.
    public static BigDecimal convertBytesToMB(BigInteger fileSize) {
        BigDecimal fileSizeDecimal = BigDecimal.valueOf(fileSize.longValue());
        return fileSizeDecimal.divide(unit, 2, RoundingMode.HALF_UP);
    }
}