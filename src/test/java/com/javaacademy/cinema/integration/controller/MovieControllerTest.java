package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.repository.movie.MovieRepository;
import com.javaacademy.cinema.repository.session.SessionRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.javaacademy.cinema.repository.movie.MovieRepositoryImpl.SQL_QUERY_COUNT_ALL_MOVIES;
import static com.javaacademy.cinema.repository.movie.MovieRepositoryImpl.SQL_QUERY_CURRENT_SEQ_MOVIE_ID;
import static com.javaacademy.cinema.service.authorization.AuthorizationServiceImpl.SECRET_TOKEN_CHECK_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayName("Тестирование эндпоинтов контроллера MovieController")
public class MovieControllerTest {
    public static final String MOVIE_PATH = "/api/v1/movie";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SessionRepository sessionRepository;

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(MOVIE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final RequestSpecification requestSpecAuth = new RequestSpecBuilder()
            .setBasePath(MOVIE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .addHeader("user-token", "secretadmin123")
            .build();
    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Успешное получение списка фильмов кинотеатра")
    public void getMoviesSuccess() {
        Integer countMovie = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_ALL_MOVIES,
                Integer.class);

        List<MovieDto> movieDto = RestAssured.given(requestSpec)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(countMovie, movieDto.size());
    }

    @Test
    @DisplayName("Успешное создание фильма админом кинотеатра")
    public void createMovieSuccess() {
        String localDateTime = LocalDateTime.now().toString();
        String nameExpected = "name - автотест от: %s".formatted(localDateTime);
        String descriptionExpected = "description - автотест от: %s".formatted(localDateTime);
        MovieAdminDto movieAdminDtoRq = new MovieAdminDto(null, nameExpected, descriptionExpected);

        MovieAdminDto movieAdminDtoRs = RestAssured.given(requestSpecAuth)
                .body(movieAdminDtoRq)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer lastMovieId = jdbcTemplate.queryForObject(
                SQL_QUERY_CURRENT_SEQ_MOVIE_ID,
                Integer.class);

        assertEquals(nameExpected, movieAdminDtoRs.getName());
        assertEquals(descriptionExpected, movieAdminDtoRs.getDescription());
        assertEquals(lastMovieId, movieAdminDtoRs.getId());
    }

    @Test
    @DisplayName("Нет админского заголовка - отдан 403 код состояния")
    public void checkSecretHeaderFailure() {
        MovieAdminDto movieAdminDtoRq = new MovieAdminDto(null, "test", "test");

        RestAssured.given(requestSpec)
                .body(movieAdminDtoRq)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает API key админского заголовка - отдан 403 код состояния")
    public void checkSecretTokenEmptyFailure() {
        MovieAdminDto movieAdminDtoRq = new MovieAdminDto(null, "test", "test");

        RestAssured.given(requestSpec)
                .header("user-token", UUID.randomUUID().toString())
                .body(movieAdminDtoRq)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }
}
