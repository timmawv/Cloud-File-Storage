package avlyakulov.timur.CloudFileStorage.minio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirRequest {

    private String dirName;

    private String path;
}