package avlyakulov.timur.CloudFileStorage.config.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    private final String minioHost = System.getenv("MINIO_HOST");
    private final String minioLogin = System.getenv("MINIO_LOGIN");
    private final String minioPassword = System.getenv("MINIO_PASSWORD");

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioHost)
                .credentials(minioLogin, minioPassword)
                .build();
    }
}