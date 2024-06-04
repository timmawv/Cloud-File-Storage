package avlyakulov.timur.CloudFileStorage.util.csv_parser;

import avlyakulov.timur.CloudFileStorage.minio.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.util.strings.StringFileUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CsvFileParser {

    private static final String defaultUrlFile = "https://cdn-icons-png.flaticon.com/512/2258/2258853.png";

    private static final Map<String, String> fileFormats = new ConcurrentHashMap<>();

    static {
        fillFileFormats();
    }

    private static void fillFileFormats()  {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("file_icon.csv");
             CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(is)))) {
                String[] nextRecord;
                reader.readNext();
                while ((nextRecord = reader.readNext()) != null) {
                    fileFormats.put(nextRecord[0], nextRecord[1]);
                }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("something went wrong with parsing icon file " + e.getMessage());
        }
    }

    public static void setFileIconForFile(FileResponse fileResponse) {
        if (!fileResponse.getIsDirectory()) {
            String fileFormat = StringFileUtils.getFileType(fileResponse.getObjectName());
            fileResponse.setFileIcon(fileFormats.getOrDefault(fileFormat, defaultUrlFile));
            return;
        }
        fileResponse.setFileIcon(fileFormats.getOrDefault(".dir", defaultUrlFile));
    }
}