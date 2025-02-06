package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.mapper.CinemaClientMapper;
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

import static com.javaacademy.cinema.integration.controller.CinemaAdminControllerTest.MOVIE_PATH;
import static com.javaacademy.cinema.integration.controller.CinemaAdminControllerTest.SESSION_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера CinemaClientController")
public class CinemaClientControllerTest {
    public static final String BASE_PATH = "/api/v1";
    public static final String FREE_PLACES_PATH = "/session/%s/free-place";
    public static final String TICKET_BOOKING_PATH = "/ticket/booking";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CinemaClientMapper cinemaClientMapper;
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
        Integer countMovie = jdbcTemplate.queryForObject("""
                        select count(*)
                        from movie;""",
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
        Integer countSession = jdbcTemplate.queryForObject("""
                        select count(*)
                        from session;""",
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
        Integer lastSession = jdbcTemplate.queryForObject("""
                        select id
                        from session
                        order by id desc
                        limit 1;""",
                Integer.class);
        Integer countFreePlacesOnSession = jdbcTemplate.queryForObject("""
                        select count(*)
                        from ticket
                        where session_id = %s and is_purchased = false;""".formatted(lastSession),
                Integer.class);

        List<String> freePlaces = RestAssured.given(requestSpec)
                .get(FREE_PLACES_PATH.formatted(lastSession))
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
    @DisplayName("Успешное получение списка списка свободных мест на сеанс кинотеатра")
    public void getBuyTicketSuccess() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Session lastSession = jdbcTemplate.queryForObject("""
                        select *
                        from session
                        order by id desc
                        limit 1;""",
                sessionRepository::mapToSession);
        Place lastPlace = jdbcTemplate.queryForObject("""
                        select *
                        from place
                        order by id desc
                        limit 1;""",
                placeRepository::mapToPlace);
        Ticket expected = jdbcTemplate.queryForObject("""
                        select *
                        from ticket
                        where session_id = ? and place_id = ?
                        order by id asc
                        limit 1;""",
                ticketRepository::mapToTicket,
                lastSession.getId(),
                lastPlace.getId());
        assertFalse(expected.isSold());
        BookingDtoRq bookingDtoRq = new BookingDtoRq(lastSession.getId(), lastPlace.getName());

        BookingDtoRs actual = RestAssured.given(requestSpec)
                .body(bookingDtoRq)
                .post(TICKET_BOOKING_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPlace().getName(), actual.getPlace());
        assertEquals(lastSession.getMovie().getName(), actual.getMovieName());
        assertEquals(lastSession.getDateTime().format(formatter), actual.getDate());
    }
}
