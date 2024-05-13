package avlyakulov.timur.CloudFileStorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFileNameDto {

    private String newFileName;

    private String oldFilePath;

    private String oldFileName;

    private Boolean isFileDirectory;
}