package com.javaacademy.cinema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi() {

        Contact myContact = new Contact()
                .name("Александр Акимов")
                .email("my.email@example.com");

        Info info = new Info()
                .title("API кинотеатра")
                .version("1.0")
                .description("Этот API предоставляет эндпоинты администрации кинотеатра для заведения фильмов и "
                        + "сеансов на них. Клиенты кинотеатра могут просматривать информацию о фильмах и сеансах, "
                        + "свободных местах и покупать и билеты на них.")
                .contact(myContact);

        return new OpenAPI()
                .info(info);
    }
}
