package com.javaacademy.cinema.integration.controller;

import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.repository.ticket.TicketRepository;
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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.javaacademy.cinema.repository.place.PlaceRepositoryImpl.SQL_QUERY_PLACE_NAME_NOT_SOLD_LAST_TICKET;
import static com.javaacademy.cinema.repository.session.SessionRepositoryImpl.SQL_QUERY_SESSION_IN_LAST_SOLD_TICKET;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_COUNT_SOLD_TICKETS_ON_SESSION;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_FIND_LAST_TICKET_ID;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_SESSION_ID_NOT_SOLD_LAST_TICKET;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_SOLD_TICKETS_IS_EXISTS;
import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.SQL_QUERY_SOLD_TICKET_BY_SESSION_ID_AND_NAME;
import static com.javaacademy.cinema.service.authorization.AuthorizationServiceImpl.SECRET_TOKEN_CHECK_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayName("Тестирование эндпоинтов контроллера TicketController")
public class TicketControllerTest {
    public static final String BASE_PATH = "/api/v1/ticket";
    public static final String SOLD_TICKET_PATH = "/sold";
    public static final String TICKET_BOOKING_PATH = "/booking";
    public static final String QUERY_PARAM_SESSION = "session";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TicketRepository ticketRepository;

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBasePath(BASE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final RequestSpecification requestSpecAuth = new RequestSpecBuilder()
            .setBasePath(BASE_PATH)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .addHeader("user-token", "secretadmin123")
            .build();
    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Успешное получение проданных билетов на сеанс")
    public void getSoldTicketsOnSessionSuccess() {
        assertTrue(checkAndCreatePaidTicketOnSessionIfNotExists());
        Integer sessionInLastSoldTicket = jdbcTemplate.queryForObject(
                SQL_QUERY_SESSION_IN_LAST_SOLD_TICKET,
                Integer.class);
        Integer countSoldTicketsOnSession = jdbcTemplate.queryForObject(
                SQL_QUERY_COUNT_SOLD_TICKETS_ON_SESSION,
                Integer.class,
                sessionInLastSoldTicket);

        List<TicketAdminDto> soldTickets = RestAssured.given(requestSpecAuth)
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
        assertEquals(sessionInLastSoldTicket, soldTickets.stream().findFirst().orElseThrow().getSession());
    }

    @Test
    @DisplayName("Успешная покупка билета на сеанс и по месту")
    public void getBuyTicketSuccess() {
        Integer expectedSessionId = jdbcTemplate.queryForObject(
                SQL_QUERY_SESSION_ID_NOT_SOLD_LAST_TICKET,
                Integer.class);
        String expectedPlaceName = jdbcTemplate.queryForObject(
                SQL_QUERY_PLACE_NAME_NOT_SOLD_LAST_TICKET,
                String.class);
        Ticket expectedTicket = ticketRepository.findTicketBySessionIdAndPlaceName(
                expectedSessionId, expectedPlaceName).orElseThrow();
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
        assertEquals(expectedTicket.getSession().getDateTime()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), actualTicket.getDate());
    }

    @Test
    @DisplayName("Нет админского заголовка - отдан 403 код состояния")
    public void checkSecretHeaderFailure() {
        Integer sessionId = 0;
        RestAssured.given(requestSpec)
                .queryParam(QUERY_PARAM_SESSION, sessionId)
                .get(SOLD_TICKET_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    @Test
    @DisplayName("Не совпадает админский заголовок - отдан 403 код состояния")
    public void checkSecretTokenEmptyFailure() {
        Integer sessionId = 0;

        RestAssured.given(requestSpec)
                .header("user-token", UUID.randomUUID().toString())
                .queryParam(QUERY_PARAM_SESSION, sessionId)
                .get(SOLD_TICKET_PATH)
                .then()
                .spec(responseSpec)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(SECRET_TOKEN_CHECK_FAILED));
    }

    private boolean checkAndCreatePaidTicketOnSessionIfNotExists() {
        if (checkTicketIsSoldIsExists()) {
            return true;
        } else {
            Integer lastTicketId = jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_LAST_TICKET_ID,
                    Integer.class);
            jdbcTemplate.update(SQL_QUERY_SOLD_TICKET_BY_SESSION_ID_AND_NAME, lastTicketId);
        }
        return checkTicketIsSoldIsExists();
    }

    private boolean checkTicketIsSoldIsExists() {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                SQL_QUERY_SOLD_TICKETS_IS_EXISTS,
                Boolean.class));
    }
}
