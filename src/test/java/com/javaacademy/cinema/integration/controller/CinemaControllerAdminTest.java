package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
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

import static com.javaacademy.cinema.service.SecurityHelper.SECRET_TOKEN_CHECK_FAILED;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.COUNT_ALL_PLACES;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.COUNT_SOLD_TICKETS;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_MOVIE_ID;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_PLACE_ID;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_SESSION_ID;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_SESSION_WITH_SOLD_TICKET;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_TICKET_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера CinemaAdminController")
public class CinemaControllerAdminTest {
    public static final String BASE_ADMIN_PATH = "/api/v1";
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
    @Autowired
    private TicketRepository ticketRepository;

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(BASE_ADMIN_PATH)
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
        Integer lastMovieId = getLastMovieId();
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
        lastMovieId++;
        Movie actualMovie = movieRepository.findById(lastMovieId).get();

        assertEquals(nameExpected, movieAdminDtoRs.getName());
        assertEquals(descriptionExpected, movieAdminDtoRs.getDescription());

        assertEquals(nameExpected, actualMovie.getName());
        assertEquals(descriptionExpected, actualMovie.getDescription());
    }

    @Test
    @DisplayName("Успешное создание сеанса фильма админом кинотеатра")
    public void createSessionSuccess() {
        Movie expectedMovie = movieRepository.findById(getLastMovieId()).get();
        Integer expectedLastSessionId = lastSessionId() + 1;
        Integer countPlace = jdbcTemplate.queryForObject(
                COUNT_ALL_PLACES.getSqlQuery(),
                Integer.class);
        Integer lastTicketId = lastTicketId();
        Integer expectedLastPlaceId = jdbcTemplate.queryForObject(
                LAST_PLACE_ID.getSqlQuery(),
                Integer.class);

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
        Session actualSession = sessionRepository.findById(lastSessionId()).get();
        Ticket actualLastTicket = ticketRepository.findById(lastTicketId()).get();

        assertEquals(expectedLastSessionId, actualSession.getId());
        assertEquals(formattedDateTime, actualSession.getDateTime().format(formatter));
        assertEquals(new BigDecimal("500.00"), actualSession.getPrice());
        assertEquals(expectedMovie, actualSession.getMovie());

        assertEquals(lastTicketId + countPlace, actualLastTicket.getId());
        assertEquals(expectedLastPlaceId, actualLastTicket.getPlace().getId());
        assertFalse(actualLastTicket.isSold());
    }

    @Test
    @DisplayName("Успешное получение проданных билетов на сеанс")
    public void getSoldTicketsOnSessionSuccess() {
        Integer lastSessionWithSoldTicket = jdbcTemplate.queryForObject(
                LAST_SESSION_WITH_SOLD_TICKET.getSqlQuery(),
                Integer.class);
        Integer countSoldTickets = jdbcTemplate.queryForObject(
                COUNT_SOLD_TICKETS.getSqlQuery(),
                Integer.class,
                lastSessionWithSoldTicket);

        List<TicketAdminDto> soldTickets = RestAssured.given(requestSpec)
                .queryParam(QUERY_PARAM_SESSION, lastSessionWithSoldTicket)
                .get(SOLD_TICKET_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(countSoldTickets, soldTickets.size());
        assertEquals(lastSessionWithSoldTicket, soldTickets.stream().findFirst().get().getSession());
    }

    @Test
    @DisplayName("Нет секретного заголовка - отдан 403 код состояния")
    public void checkSecretHeaderFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .log(LogDetail.ALL)
                .build();
        Integer lastSessionId = lastSessionId();

        RestAssured.given(requestSpecSecret)
                .queryParam("session", lastSessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает секретный заголовок - отдан 403 код состояния")
    public void checkSecretTokenEmptyFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .addHeader("user-token", UUID.randomUUID().toString())
                .log(LogDetail.ALL)
                .build();
        Integer lastSessionId = lastSessionId();

        RestAssured.given(requestSpecSecret)
                .queryParam("session", lastSessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    private Integer getLastMovieId() {
        return jdbcTemplate.queryForObject(LAST_MOVIE_ID.getSqlQuery(), Integer.class);
    }

    private Integer lastSessionId() {
        return jdbcTemplate.queryForObject(LAST_SESSION_ID.getSqlQuery(), Integer.class);
    }

    private Integer lastTicketId() {
        return jdbcTemplate.queryForObject(LAST_TICKET_ID.getSqlQuery(), Integer.class);
    }
}
