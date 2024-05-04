package avlyakulov.timur.CloudFileStorage.mapper;

import avlyakulov.timur.CloudFileStorage.csv_parser.CsvFileParser;
import avlyakulov.timur.CloudFileStorage.dto.FileResponse;
import avlyakulov.timur.CloudFileStorage.util.validator.FileSizeConverter;
import io.minio.messages.Item;
import org.mapstruct.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper
public interface FileMapper {

    @Mapping(target = "objectName", expression = "java(item.objectName())")
    @Mapping(target = "directory", expression = "java(item.isDir())")
    FileResponse mapItemToResponse(Item item);

    @BeforeMapping
    default void convertFileSize(Item item, @MappingTarget FileResponse fileResponse) {
        fileResponse.setSize(String.valueOf(item.size()));
    }

    @BeforeMapping
    default void convertDateToPattern(Item item, @MappingTarget FileResponse fileResponse) {
        ZonedDateTime lastModified = item.lastModified();
        lastModified = lastModified.withZoneSameInstant(ZoneId.of("Europe/Kiev"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm");
        String lastModifiedFormatted = lastModified.format(formatter);
        fileResponse.setLastModified(lastModifiedFormatted);
    }

    @AfterMapping
    default void convertFile(@MappingTarget FileResponse fileResponse) {
        int indexPath = fileResponse.getObjectName().lastIndexOf("/");
        fileResponse.setObjectName(fileResponse.getObjectName().substring(indexPath + 1));
        fileResponse.setSize(FileSizeConverter.convertFileSize(fileResponse.getSize()));
    }

    List<FileResponse> mapFileToResponse(List<Item> files);

    @AfterMapping
    default void addUrlIconFile(@MappingTarget List<FileResponse> fileResponses) {
        fileResponses = fileResponses.stream().map(CsvFileParser::setFileFormatForFile).toList();
    }
}