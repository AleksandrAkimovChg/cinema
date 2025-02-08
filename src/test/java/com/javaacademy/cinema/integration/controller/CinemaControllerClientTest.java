package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.mapper.CinemaMapper;
import com.javaacademy.cinema.repository.PlaceRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.javaacademy.cinema.integration.controller.CinemaControllerAdminTest.MOVIE_PATH;
import static com.javaacademy.cinema.integration.controller.CinemaControllerAdminTest.SESSION_PATH;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.COUNT_ALL_MOVIES;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.COUNT_ALL_SESSIONS;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.COUNT_FREE_PLACES_ON_SESSION;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_SESSION_ID;
import static com.javaacademy.cinema.integration.controller.TestUtilSqlQuery.LAST_TICKET_NOT_SOLD_BY_LAST_SESSION_ID_AND_LAST_PLACE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера CinemaClientController")
public class CinemaControllerClientTest {
    public static final String BASE_PATH = "/api/v1";
    public static final String FREE_PLACES_PATH = "/session/%s/free-place";
    public static final String TICKET_BOOKING_PATH = "/ticket/booking";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PlaceRepository placeRepository;

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(BASE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();


    @Test
    @DisplayName("Успешное получение списка фильмов кинотеатра")
    public void getMoviesSuccess() {
        Integer countMovie = jdbcTemplate.queryForObject(
                COUNT_ALL_MOVIES.getSqlQuery(),
                Integer.class);

        List<MovieDto> movieDto = RestAssured.given(requestSpec)
                .get(MOVIE_PATH)
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
    @DisplayName("Успешное получение списка сеансов кинотеатра")
    public void getSessionsSuccess() {
        Integer countSession = jdbcTemplate.queryForObject(
                COUNT_ALL_SESSIONS.getSqlQuery(),
                Integer.class);

        List<SessionDto> sessionDtos = RestAssured.given(requestSpec)
                .get(SESSION_PATH)
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
    @DisplayName("Успешное получение списка списка свободных мест на сеанс кинотеатра")
    public void getFreePLacesSuccess() {
        Integer lastSessionId = jdbcTemplate.queryForObject(
                LAST_SESSION_ID.getSqlQuery(),
                Integer.class);
        Integer countFreePlacesOnSession = jdbcTemplate.queryForObject(
                COUNT_FREE_PLACES_ON_SESSION.getSqlQuery(),
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
    @DisplayName("Успешная покупка билета на сеанс и по месту")
    public void getBuyTicketSuccess() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Ticket expectedTicket = jdbcTemplate.queryForObject(
                LAST_TICKET_NOT_SOLD_BY_LAST_SESSION_ID_AND_LAST_PLACE_ID.getSqlQuery(),
                ticketRepository::mapToTicket);
        BookingDtoRq bookingDtoRq = new BookingDtoRq(
                expectedTicket.getSession().getId(),
                expectedTicket.getPlace().getName());

        BookingDtoRs actualTicket = RestAssured.given(requestSpec)
                .body(bookingDtoRq)
                .post(TICKET_BOOKING_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(expectedTicket.getId(), actualTicket.getTicketId());
        assertEquals(expectedTicket.getPlace().getName(), actualTicket.getPlace());
        assertEquals(expectedTicket.getSession().getMovie().getName(), actualTicket.getMovieName());
        assertEquals(expectedTicket.getSession().getDateTime().format(formatter), actualTicket.getDate());
    }
}
