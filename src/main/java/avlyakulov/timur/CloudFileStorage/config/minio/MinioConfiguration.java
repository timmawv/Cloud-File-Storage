package avlyakulov.timur.CloudFileStorage.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.host}")
    private String minioHost;

    @Value("${minio.login}")
    private String minioLogin;

    @Value("${minio.password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioHost)
                .credentials(minioLogin, minioPassword)
                .build();
    }
}