package gameofthreads.schedules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SchedulesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulesApplication.class, args);
    }
}
