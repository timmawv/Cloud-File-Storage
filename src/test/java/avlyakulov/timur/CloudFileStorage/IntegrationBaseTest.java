package avlyakulov.timur.CloudFileStorage;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@TestConfiguration
public class IntegrationBaseTest {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        var mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
        return mysql;
    }

    @Bean
    public GenericContainer<?> minioContainer(DynamicPropertyRegistry registry) {
        var minio = new GenericContainer<>(DockerImageName.parse("quay.io/minio/minio"))
                .withEnv(Map.of("MINIO_ROOT_USER", "minioadmin", "MINIO_ROOT_PASSWORD", "minioadmin"))
                .withExposedPorts(9000, 9001)
                .withCommand("server", "/data", "--console-address", ":9001");

        registry.add("minio.host", minio::getHost);
        registry.add("minio.port", minio::getFirstMappedPort);
        registry.add("minio.login", () -> minio.getEnvMap().get("MINIO_ROOT_USER"));
        registry.add("minio.password", () -> minio.getEnvMap().get("MINIO_ROOT_PASSWORD"));
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> 6379);
        return minio;
    }
}