package avlyakulov.timur.CloudFileStorage.util.converter;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class FileSizeConverter {

    private static final BigDecimal unit = BigDecimal.valueOf(1024 * 1024);

    public static void convertFileSize(FileResponse fileResponse) {
        BigDecimal fileSizeDecimal = new BigDecimal(fileResponse.getSize());
        fileResponse.setSize(fileSizeDecimal.divide(unit, 2, RoundingMode.HALF_UP).toString());
    }

    public static BigDecimal convertBytesToMB(BigInteger fileSize) {
        BigDecimal fileSizeDecimal = BigDecimal.valueOf(fileSize.longValue());
        return fileSizeDecimal.divide(unit, 2, RoundingMode.HALF_UP);
    }
}