package avlyakulov.timur.CloudFileStorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CloudFileStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudFileStorageApplication.class, args);
	}
}