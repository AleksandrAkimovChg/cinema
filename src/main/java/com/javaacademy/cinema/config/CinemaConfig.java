package com.javaacademy.cinema.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CinemaSecurityProperty.class})
public class CinemaConfig {
}
