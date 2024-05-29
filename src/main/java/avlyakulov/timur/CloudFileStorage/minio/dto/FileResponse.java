package avlyakulov.timur.CloudFileStorage.minio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {

    private String objectName;

    private String filePath;

    private String fileType;

    private String size;

    private String lastModified;

    private Boolean isDirectory;

    private String fileIcon;
}