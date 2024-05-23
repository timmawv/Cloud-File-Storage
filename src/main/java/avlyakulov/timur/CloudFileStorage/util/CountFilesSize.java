package avlyakulov.timur.CloudFileStorage.util;

import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

public class CountFilesSize {

    public static BigInteger countFileSize(List<MultipartFile> files) {
        return BigInteger.valueOf(files.stream()
                .map(MultipartFile::getSize)
                .reduce(0L, Long::sum));
    }

    public static BigInteger countItemSize(List<Item> files) {
        return BigInteger.valueOf(files.stream()
                .map(Item::size)
                .reduce(0L, Long::sum));
    }
}