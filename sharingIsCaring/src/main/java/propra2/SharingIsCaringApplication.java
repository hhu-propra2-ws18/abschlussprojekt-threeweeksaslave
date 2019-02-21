package propra2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class SharingIsCaringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharingIsCaringApplication.class, args);
    }
}
