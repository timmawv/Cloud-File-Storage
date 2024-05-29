package avlyakulov.timur.CloudFileStorage.minio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileRenameRequest {

    private String oldFileName;

    private String oldFilePath;

    private String newFileName;

    private Boolean isFileDirectory;
}