package avlyakulov.timur.CloudFileStorage.util.converter;

public class PathToBreadcrumbConverter {

    public static String[] convertPathToBreadcrumb(String pathFromUrl) {
        String[] split = pathFromUrl.split("/");
        String[] links = new String[split.length];
        for (int i = 0; i < links.length; ++i) {
            int counter = 0;
            if (i == 0) {
                links[i] = split[i].concat("/");
                continue;
            }
            StringBuilder stringBuilder = new StringBuilder();
            while (counter != i + 1) {
                stringBuilder.append(split[counter].concat("/"));
                ++counter;
            }
            links[i] = stringBuilder.toString();
        }
        return links;
    }
}
