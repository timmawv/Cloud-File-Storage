package avlyakulov.timur.CloudFileStorage.util.strings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringFileUtilsTest {


    @Test
    void getPathToObjectDirectory_fileNotDir_pathHasFolders() {
        String filePath = "/folder/folder/img.png";
        Boolean isDir = false;

        String pathToObjectDirectory = StringFileUtils.getPathToObjectDirectory(filePath, isDir);
        assertThat(pathToObjectDirectory).isEqualTo("/folder/folder/");
    }

    @Test
    void getPathToObjectDirectory_fileNotDir_pathHasNotFolders() {
        String filePath = "img.png";
        Boolean isDir = false;

        String pathToObjectDirectory = StringFileUtils.getPathToObjectDirectory(filePath, isDir);
        assertThat(pathToObjectDirectory).isEqualTo("");
    }

    @Test
    void getPathToObjectDirectory_fileIsDir_pathHasFolders() {
        String filePath = "/folder/folder/folder/";
        Boolean isDir = true;

        String pathToObjectDirectory = StringFileUtils.getPathToObjectDirectory(filePath, isDir);
        assertThat(pathToObjectDirectory).isEqualTo("/folder/folder/");
    }

    @Test
    void getPathToObjectDirectory_fileIsDir_pathHasNotFolders() {
        String filePath = "folder/";
        Boolean isDir = true;

        String pathToObjectDirectory = StringFileUtils.getPathToObjectDirectory(filePath, isDir);
        assertThat(pathToObjectDirectory).isEqualTo("");
    }

    @Test
    void getFileType_fileHasType() {
        String fileName = "img.png";

        String fileType = StringFileUtils.getFileType(fileName);

        assertThat(fileType).isEqualTo(".png");
    }

    @Test
    void getFileType_fileHasNotType() {
        String fileName = "just-file";

        String fileType = StringFileUtils.getFileType(fileName);

        assertThat(fileType).isEqualTo("");
    }
}