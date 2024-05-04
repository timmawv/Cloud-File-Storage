package avlyakulov.timur.CloudFileStorage.csv_parser;

import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CsvFileParser {

    //todo здесь может возникнуть проблема с "/" потому что на маках это вроде другое.
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

    public static FileResponse setFileFormatForFile(FileResponse fileResponse) {
        int lastIndexFormat = fileResponse.getObjectName().lastIndexOf(".");
        String fileFormat = fileResponse.getObjectName().substring(lastIndexFormat + 1).toLowerCase();
        fileResponse.setFileIcon(fileFormats.getOrDefault(fileFormat, defaultUrlFile));
        return fileResponse;
    }
}