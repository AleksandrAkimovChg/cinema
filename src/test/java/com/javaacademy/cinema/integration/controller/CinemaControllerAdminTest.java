package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.SessionRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.javaacademy.cinema.integration.controller.CinemaControllerClientTest.BASE_PATH;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.COUNT_ALL_PLACES;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.COUNT_ALL_TICKET_ON_SESSION;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.COUNT_SOLD_TICKETS_ON_SESSION;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.CURRENT_SEQ_MOVIE_ID;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.CURRENT_SEQ_SESSION_ID;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.LAST_MOVIE_ID;
import static com.javaacademy.cinema.integration.controller.TestHelperSqlQuery.SESSION_IN_LAST_SOLD_TICKET;
import static com.javaacademy.cinema.service.SecurityHelper.SECRET_TOKEN_CHECK_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("Тестирование эндпоинтов контроллера CinemaController с доступом администратора")
public class CinemaControllerAdminTest {
    public static final String MOVIE_PATH = "/movie";
    public static final String SESSION_PATH = "/session";
    public static final String SOLD_TICKET_PATH = "/ticket/sold";
    public static final String QUERY_PARAM_SESSION = "session";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SessionRepository sessionRepository;


    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(BASE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .addHeader("user-token", "secretadmin123")
            .build();
    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Успешное создание фильма админом кинотеатра")
    public void createMovieSuccess() {
        final String nameExpected = "Тест успешное создание фильма - name";
        final String descriptionExpected = "Тест успешное создание фильма - description";
        MovieAdminDto movieAdminDtoRq = new MovieAdminDto(null, nameExpected, descriptionExpected);

        MovieAdminDto movieAdminDtoRs = RestAssured.given(requestSpec)
                .body(movieAdminDtoRq)
                .post(MOVIE_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer lastMovieId = jdbcTemplate.queryForObject(
                CURRENT_SEQ_MOVIE_ID.getSqlQuery(),
                Integer.class);

        assertEquals(nameExpected, movieAdminDtoRs.getName());
        assertEquals(descriptionExpected, movieAdminDtoRs.getDescription());
        assertEquals(lastMovieId, movieAdminDtoRs.getId());
    }

    @Test
    @DisplayName("Успешное создание сеанса фильма админом кинотеатра")
    public void createSessionSuccess() {
        Integer lastMovieId = jdbcTemplate.queryForObject(
                LAST_MOVIE_ID.getSqlQuery(),
                Integer.class);
        Movie expectedMovie = movieRepository.findById(lastMovieId).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        SessionAdminDto sessionAdminDto = new SessionAdminDto(
                formattedDateTime,
                new BigDecimal("500.00"),
                expectedMovie.getId());

        RestAssured.given(requestSpec)
                .body(sessionAdminDto)
                .post(SESSION_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CREATED.value());
        Integer lastSessionId = jdbcTemplate.queryForObject(
                CURRENT_SEQ_SESSION_ID.getSqlQuery(),
                Integer.class);
        Session actualSession = sessionRepository.findById(lastSessionId).get();
        Integer countTicketOnSession = jdbcTemplate.queryForObject(
                COUNT_ALL_TICKET_ON_SESSION.getSqlQuery(),
                Integer.class,
                actualSession.getId());
        Integer countPlaces = jdbcTemplate.queryForObject(
                COUNT_ALL_PLACES.getSqlQuery(),
                Integer.class);

        assertEquals(formattedDateTime, actualSession.getDateTime().format(formatter));
        assertEquals(new BigDecimal("500.00"), actualSession.getPrice());
        assertEquals(expectedMovie, actualSession.getMovie());
        assertEquals(countPlaces, countTicketOnSession);
    }

    @Test
    @DisplayName("Успешное получение проданных билетов на сеанс")
    public void getSoldTicketsOnSessionSuccess() {
        Integer sessionInLastSoldTicket = jdbcTemplate.queryForObject(
                SESSION_IN_LAST_SOLD_TICKET.getSqlQuery(),
                Integer.class);
        Integer countSoldTicketsOnSession = jdbcTemplate.queryForObject(
                COUNT_SOLD_TICKETS_ON_SESSION.getSqlQuery(),
                Integer.class,
                sessionInLastSoldTicket);

        List<TicketAdminDto> soldTickets = RestAssured.given(requestSpec)
                .queryParam(QUERY_PARAM_SESSION, sessionInLastSoldTicket)
                .get(SOLD_TICKET_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(countSoldTicketsOnSession, soldTickets.size());
        assertEquals(sessionInLastSoldTicket, soldTickets.stream().findFirst().get().getSession());
    }

    @Test
    @DisplayName("Нет админского заголовка - отдан 403 код состояния")
    public void checkSecretHeaderFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .log(LogDetail.ALL)
                .build();
        Integer sessionId = 0;

        RestAssured.given(requestSpecSecret)
                .queryParam(QUERY_PARAM_SESSION, sessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает админский заголовок - отдан 403 код состояния")
    public void checkSecretTokenEmptyFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .addHeader("user-token", UUID.randomUUID().toString())
                .log(LogDetail.ALL)
                .build();
        Integer sessionId = 0;

        RestAssured.given(requestSpecSecret)
                .queryParam(QUERY_PARAM_SESSION, sessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }
}
