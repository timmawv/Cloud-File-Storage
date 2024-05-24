package avlyakulov.timur.CloudFileStorage.util.converter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathToBreadcrumbConverterTest {

    @Test
    void convertPathToBreadcrumb() {
        String[] breadcrumbCorrect = {"folder/", "folder/folder/", "folder/folder/folder/"};
        String path = "folder/folder/folder/";

        String[] breadcrumb = PathToBreadcrumbConverter.convertPathToBreadcrumb(path);

        assertThat(breadcrumb).isEqualTo(breadcrumbCorrect);
    }
}