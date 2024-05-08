package avlyakulov.timur.CloudFileStorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {

    private String objectName;

    private String size;

    private String lastModified;

    private Boolean directory;

    private String fileIcon;

    private String file;
}