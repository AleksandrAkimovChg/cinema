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

import static com.javaacademy.cinema.controller.CinemaAdminController.SECRET_TOKEN_CHECK_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера CinemaAdminController")
public class CinemaAdminControllerTest {
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
        Integer lastMovieId = jdbcTemplate.queryForObject("""
                        select id
                        from movie
                        order by id desc
                        limit 1;""",
                Integer.class);
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
        Integer lastMovieId = jdbcTemplate.queryForObject("""
                        select id
                        from movie
                        order by id desc
                        limit 1;""",
                Integer.class);
        Movie expectedMovie = movieRepository.findById(lastMovieId).get();
        Integer lastSessionId = jdbcTemplate.queryForObject("""
                        select id
                        from session
                        order by id desc
                        limit 1;""",
                Integer.class);
        Integer countPlace = jdbcTemplate.queryForObject("""
                        select count(*)
                        from place;""",
                Integer.class);
        Integer lastTicketId = jdbcTemplate.queryForObject("""
                        select id
                        from ticket
                        order by id desc
                        limit 1;""",
                Integer.class);
        Integer lastPlaceId = jdbcTemplate.queryForObject("""
                        select id
                        from place
                        order by id desc
                        limit 1;""",
                Integer.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        SessionAdminDto sessionAdminDto = new SessionAdminDto(
                formattedDateTime,
                new BigDecimal("500.00"),
                lastMovieId);

        RestAssured.given(requestSpec)
                .body(sessionAdminDto)
                .post(SESSION_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CREATED.value());

        Integer actualLastSessionId = jdbcTemplate.queryForObject("""
                        select id
                        from session
                        order by id desc
                        limit 1;""",
                Integer.class);
        Session actualSession = sessionRepository.findById(actualLastSessionId).get();
        lastSessionId++;
        assertEquals(lastSessionId, actualSession.getId());
        assertEquals(formattedDateTime, actualSession.getDateTime().format(formatter));
        assertEquals(new BigDecimal("500.00"), actualSession.getPrice());
        assertEquals(expectedMovie, actualSession.getMovie());

        Integer actualLastTicketId = jdbcTemplate.queryForObject("""
                        select id
                        from ticket
                        order by id desc
                        limit 1;""",
                Integer.class);
        Ticket actualLastTicket = ticketRepository.findById(actualLastTicketId).get();
        assertEquals(lastTicketId + countPlace, actualLastTicketId);
        assertEquals(lastPlaceId, actualLastTicket.getPlace().getId());
        assertFalse(actualLastTicket.isSold());
    }

    @Test
    @DisplayName("Успешное получение проданных билетов на сеанс")
    public void getSoldTicketsOnSessionSuccess() {
        Integer lastSessionWithSoldTicket = jdbcTemplate.queryForObject("""
                        select distinct session_id
                        from ticket
                        where is_purchased = true
                        order by session_id desc
                        limit 1;""",
                Integer.class);
        Integer countSoldTicket = jdbcTemplate.queryForObject("""
                        select count(*)
                        from ticket
                        where session_id = %s and is_purchased = true;""".formatted(lastSessionWithSoldTicket),
                Integer.class);

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

        assertEquals(countSoldTicket, soldTickets.size());
        assertEquals(lastSessionWithSoldTicket, soldTickets.stream().findFirst().get().getSession());
    }

    @Test
    @DisplayName("Нет секретного заголовка - отдан 409 код состояния")
    public void checkSecretHeaderFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .log(LogDetail.ALL)
                .build();
        String lastSessionQuery = "";
        Integer lastSessionId = jdbcTemplate.queryForObject("""
                        select id
                        from session
                        order by id desc
                        limit 1;""",
                Integer.class);

        RestAssured.given(requestSpecSecret)
                .queryParam("session", lastSessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает секретный заголовок - отдан 409 код состояния")
    public void checkSecretTokenEmptyFailure() {
        final RequestSpecification requestSpecSecret = new RequestSpecBuilder()
                .setBasePath("/api/v1/ticket/sold")
                .addHeader("user-token", UUID.randomUUID().toString())
                .log(LogDetail.ALL)
                .build();
        Integer lastSessionId = jdbcTemplate.queryForObject("""
                        select id
                        from session
                        order by id desc
                        limit 1;""",
                Integer.class);

        RestAssured.given(requestSpecSecret)
                .queryParam("session", lastSessionId)
                .get()
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }
}
