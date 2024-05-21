package avlyakulov.timur.CloudFileStorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFileNameDto {

    private String oldFileName;

    private String oldFilePath;

    private String newFileName;

    private Boolean isFileDirectory;
}