package com.javaacademy.cinema.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class CinemaSecurityProperty {
    private String header;
    private String token;
}
