package com.javaacademy.cinema;

import com.javaacademy.cinema.config.CinemaSecurityProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties({CinemaSecurityProperty.class})
public class CinemaApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(CinemaApplication.class, args);

    }

}
