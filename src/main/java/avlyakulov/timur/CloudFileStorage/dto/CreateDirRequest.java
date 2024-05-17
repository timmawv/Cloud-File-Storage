package avlyakulov.timur.CloudFileStorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDirRequest {

    private String dirName;

    private String path;
}