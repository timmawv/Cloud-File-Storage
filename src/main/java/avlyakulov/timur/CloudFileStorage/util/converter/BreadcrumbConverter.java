package avlyakulov.timur.CloudFileStorage.util.converter;

public class BreadcrumbConverter {

    public static String[] convertPathToBreadcrumb(String pathFromUrl) {
        String[] split = pathFromUrl.split("/");
        String[] links = new String[split.length];

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            stringBuilder.append(split[i]).append("/");
            links[i] = stringBuilder.toString();
        }
        return links;
    }
}