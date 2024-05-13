package avlyakulov.timur.CloudFileStorage.util.csv_parser;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CsvFileParser {

    private static final String FILE_NAME = "src/main/resources/file_icon.csv";

    private static final String defaultUrlFile = "https://cdn-icons-png.flaticon.com/512/2258/2258853.png";

    private static Map<String, String> fileFormats = new HashMap<>();

    static {
        fillFileFormats();
    }

    private static void fillFileFormats() {
        try (CSVReader reader = new CSVReader(new FileReader(FILE_NAME))) {
            String[] nextRecord;
            reader.readNext();
            while ((nextRecord = reader.readNext()) != null) {
                fileFormats.put(nextRecord[0], nextRecord[1]);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public static void setFileIconForFile(FileResponse fileResponse) {
        int lastIndexFormat = fileResponse.getFilePath().lastIndexOf(".");
        String fileFormat = fileResponse.getFilePath().substring(lastIndexFormat + 1).toLowerCase();
        if (!fileResponse.isDirectory()) {
            fileResponse.setFileIcon(fileFormats.getOrDefault(fileFormat, defaultUrlFile));
            return;
        }
        fileResponse.setFileIcon(fileFormats.getOrDefault("directory", defaultUrlFile));
    }
}