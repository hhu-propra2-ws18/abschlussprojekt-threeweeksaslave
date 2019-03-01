package propra2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import propra2.storage.StorageProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(StorageProperties.class)
public class SharingIsCaringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharingIsCaringApplication.class, args);
    }
}

