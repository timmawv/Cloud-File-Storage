package avlyakulov.timur.CloudFileStorage.util.strings;

public class StringFileUtils {

    public static final String EMPTY_STRING = "";

    public static String getPathToObjectDirectory(String filePath, Boolean isDir) {
        if (isDir) {
            return filePath.replaceAll("[^/]+/$", "");
        }
        if (filePath.contains("/")) {
            int lastIndexOfSlash = filePath.lastIndexOf("/");
            return filePath.substring(0, lastIndexOfSlash + 1);
        }
        return EMPTY_STRING;
    }

    public static String getFileType(String fileName) {
        if (fileName.contains(".")) {
            int lastIndexOfDot = fileName.lastIndexOf(".");
            return fileName.substring(lastIndexOfDot);
        }
        return EMPTY_STRING;
    }
}