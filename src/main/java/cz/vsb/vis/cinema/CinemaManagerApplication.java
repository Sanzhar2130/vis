package cz.vsb.vis.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinemaManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaManagerApplication.class, args);
    }
}
