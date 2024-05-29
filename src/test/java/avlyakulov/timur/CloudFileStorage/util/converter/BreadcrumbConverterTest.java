package avlyakulov.timur.CloudFileStorage.util.converter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BreadcrumbConverterTest {

    @Test
    void convertPathToBreadcrumb() {
        String[] breadcrumbCorrect = {"folder/", "folder/folder/", "folder/folder/folder/"};
        String path = "folder/folder/folder/";

        String[] breadcrumb = BreadcrumbConverter.convertPathToBreadcrumb(path);

        assertThat(breadcrumb).isEqualTo(breadcrumbCorrect);
    }
}