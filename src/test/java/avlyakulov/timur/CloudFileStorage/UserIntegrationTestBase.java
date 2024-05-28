package avlyakulov.timur.CloudFileStorage;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@SpringBootTest
@Testcontainers
public class UserIntegrationTestBase {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Container
    private static final GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("quay.io/minio/minio"))
            .withEnv(Map.of("MINIO_ROOT_USER", "minioadmin", "MINIO_ROOT_PASSWORD", "minioadmin"))
            .withExposedPorts(9000, 9001)
            .withCommand("server", "/data", "--console-address", ":9001");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "db/changelog/main-changelog-test.xml");
        registry.add("minio.host", () -> "http://" + minio.getHost() + ":" + minio.getFirstMappedPort());
        registry.add("minio.login", () -> minio.getEnvMap().get("MINIO_ROOT_USER"));
        registry.add("minio.password", () -> minio.getEnvMap().get("MINIO_ROOT_PASSWORD"));
    }
}