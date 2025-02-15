package com.javaacademy.cinema.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@SecurityScheme(
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.APIKEY,
        name = "user-token",
        description = "Ключ API для доступа к ресурсу администрации кинотеатра"
)
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

        List<Tag> tags = new ArrayList<>(List.of(
                new Tag().name("Cinema controller")
                        .description("API для использования услуг кинотеатра зрителями: просмотр информации "
                                + "о фильмах и сеансах, поиска свободных мест и покупки билета на них."),
                new Tag().name("Cinema admin controller")
                        .description("API для управления кинотеатром администрацией: внесение фильмов, создание "
                                + "сеансов и просмотр списка купленных билетов на сеанс. Для контроля доступа "
                                + "к ресурсу используется специальный заголовок и токен.")
        ));

        return new OpenAPI()
                .info(info)
                .tags(tags);
    }
}
