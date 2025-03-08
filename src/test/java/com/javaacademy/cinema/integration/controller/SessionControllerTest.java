package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.repository.movie.MovieRepository;
import com.javaacademy.cinema.repository.session.SessionRepository;
import com.javaacademy.cinema.service.session.SessionService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.javaacademy.cinema.repository.movie.MovieRepositoryImpl.SQL_QUERY_LAST_MOVIE_ID;
import static com.javaacademy.cinema.repository.place.PlaceRepositoryImpl.SQL_QUERY_COUNT_ALL_PLACES;
import static com.javaacademy.cinema.repository.session.SessionRepositoryImpl.SQL_QUERY_COUNT_ALL_SESSIONS;
import static com.javaacademy.cinema.repository.session.SessionRepositoryImpl.SQL_QUERY_CURRENT_SEQ_SESSION_ID;
import static com.javaacademy.cinema.repository.session.SessionRepositoryImpl.SQL_QUERY_LAST_SESSION_ID;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_COUNT_ALL_TICKET_ON_SESSION;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_COUNT_FREE_PLACES_ON_SESSION;
import static com.javaacademy.cinema.service.authorization.AuthorizationServiceImpl.SECRET_TOKEN_CHECK_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayName("Тестирование эндпоинтов контроллера SessionController")
public class SessionControllerTest {
    public static final String SESSION_PATH = "/api/v1/session";
    public static final String FREE_PLACES_PATH = "/%s/free-place";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionService sessionService;

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(SESSION_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final RequestSpecification requestSpecAuth = new RequestSpecBuilder()
            .setBasePath(SESSION_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .addHeader("user-token", "secretadmin123")
            .build();
    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Успешное получение списка сеансов кинотеатра")
    public void getSessionsSuccess() {
        Integer countSession = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_ALL_SESSIONS,
                Integer.class);

        List<SessionDto> sessionDtos = RestAssured.given(requestSpec)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(countSession, sessionDtos.size());
    }

    @Test
    @DisplayName("Успешное получение списка свободных мест на сеанс кинотеатра")
    public void getFreePLacesSuccess() {
        assertTrue(checkAndCreateSessionIfNotExists());
        Integer lastSessionId = jdbcTemplate.queryForObject(
                SQL_QUERY_LAST_SESSION_ID,
                Integer.class);
        Integer countFreePlacesOnSession = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_FREE_PLACES_ON_SESSION,
                Integer.class,
                lastSessionId);

        List<String> freePlaces = RestAssured.given(requestSpec)
                .get(FREE_PLACES_PATH.formatted(lastSessionId))
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(countFreePlacesOnSession, freePlaces.size());
    }

    @Test
    @DisplayName("Успешное создание сеанса фильма админом кинотеатра")
    public void createSessionSuccess() {
        Integer lastMovieId = jdbcTemplate.queryForObject(
                SQL_QUERY_LAST_MOVIE_ID,
                Integer.class);
        Movie expectedMovie = movieRepository.findById(lastMovieId).orElseThrow();
        String formattedDateTime = LocalDateTime.now().format(formatter);
        SessionAdminDto sessionAdminDto = new SessionAdminDto(
                formattedDateTime,
                new BigDecimal("500.00"),
                expectedMovie.getId());

        RestAssured.given(requestSpecAuth)
                .body(sessionAdminDto)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CREATED.value());
        Integer lastSessionId = jdbcTemplate.queryForObject(
                SQL_QUERY_CURRENT_SEQ_SESSION_ID,
                Integer.class);
        Session actualSession = sessionRepository.findById(lastSessionId).orElseThrow();
        Integer countTicketOnSession = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_ALL_TICKET_ON_SESSION,
                Integer.class,
                actualSession.getId());
        Integer countPlaces = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_ALL_PLACES,
                Integer.class);

        assertEquals(formattedDateTime, actualSession.getDateTime().format(formatter));
        assertEquals(new BigDecimal("500.00"), actualSession.getPrice());
        assertEquals(expectedMovie, actualSession.getMovie());
        assertEquals(countPlaces, countTicketOnSession);
    }

    @Test
    @DisplayName("Нет админского заголовка - отдан 403 код состояния")
    public void checkSecretHeaderFailure() {
        String formattedDateTime = LocalDateTime.now().format(formatter);
        SessionAdminDto sessionAdminDto = new SessionAdminDto(
                formattedDateTime,
                new BigDecimal("500.00"),
                0);

        RestAssured.given(requestSpec)
                .body(sessionAdminDto)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает API key админского заголовка - отдан 403 код состояния")
    public void checkSecretTokenEmptyFailure() {
        String formattedDateTime = LocalDateTime.now().format(formatter);
        SessionAdminDto sessionAdminDto = new SessionAdminDto(
                formattedDateTime,
                new BigDecimal("500.00"),
                0);

        RestAssured.given(requestSpec)
                .header("user-token", UUID.randomUUID().toString())
                .body(sessionAdminDto)
                .post()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    private boolean checkAndCreateSessionIfNotExists() {
        if (checkSessionIsExists()) {
            return true;
        } else {
            Integer lastMovieId = jdbcTemplate.queryForObject(
                    SQL_QUERY_LAST_MOVIE_ID,
                    Integer.class);
            sessionService.createSession(new SessionAdminDto(
                    "01.01.2024 19:00",
                    new BigDecimal("500.00"),
                    lastMovieId)
            );
        }
        return checkSessionIsExists();
    }

    private boolean checkSessionIsExists() {
        return jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_ALL_SESSIONS,
                Boolean.class);
    }
}
