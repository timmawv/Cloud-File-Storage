package avlyakulov.timur.CloudFileStorage.util;

import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public class CountFilesSize {

    public static BigInteger countFileSize(Stream<MultipartFile> files) {
        return BigInteger.valueOf(files.map(MultipartFile::getSize).reduce(0L, Long::sum));
    }

    public static BigInteger countItemSize(Stream<Item> files) {
        return BigInteger.valueOf(files.map(Item::size).reduce(0L, Long::sum));
    }
}
