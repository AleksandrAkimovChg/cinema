package com.javaacademy.cinema.repository.ticket;

import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.PlaceNotFoundException;
import com.javaacademy.cinema.exception.SessionNotFoundException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.exception.TicketNotSoldException;
import com.javaacademy.cinema.repository.place.PlaceRepository;
import com.javaacademy.cinema.repository.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {
    private static final String SQL_QUERY_FIND_TICKET_BY_ID = """
            "select *
            from ticket
            where id = ?;""";
    public static final String SQL_QUERY_FIND_LAST_TICKET_ID = """
            "select id
            from ticket
            order by id desc
            limit 1;""";
    private static final String SQL_QUERY_FIND_ALL_NOT_SOLD_TICKET = """
            select *
            from ticket
            where session_id = ? and is_purchased = false;""";
    private static final String SQL_QUERY_FIND_ALL_SOLD_TICKET = """
            select *
            from ticket
            where session_id = ? and is_purchased = true;""";
    private static final String SQL_QUERY_FIND_TICKET_BY_SESSION_ID_AND_PLACE_NAME = """
            select t.* from ticket t
                inner join place p on t.place_id = p.id
            where t.session_id = ? and p.name = ?;
            """;
    private static final String SQL_QUERY_SAVE_TICKET = """
            insert into ticket (session_id, place_id, is_purchased)
            values(?, ?, ?) returning id;
            """;
    public static final String SQL_QUERY_SOLD_TICKET_BY_SESSION_ID_AND_NAME = """
            update ticket
            set is_purchased = true
            where id = ?;
            """;
    public static final String SQL_QUERY_COUNT_ALL_TICKET_ON_SESSION = """
            select count(*)
            from ticket
            where session_id = ?;
            """;
    public static final String SQL_QUERY_COUNT_SOLD_TICKETS = """
            select count(*)
            from ticket
            where is_purchased = true;
            """;
    public static final String SQL_QUERY_SOLD_TICKETS_IS_EXISTS = """
            select count(*) > 0
            from ticket
            where is_purchased = true;
            """;
    public static final String SQL_QUERY_COUNT_SOLD_TICKETS_ON_SESSION = """
            select count(*)
            from ticket
            where session_id = ? and is_purchased = true;
            """;
    public static final String SQL_QUERY_COUNT_FREE_PLACES_ON_SESSION = """
            select count(*)
            from ticket
            where session_id = ? and is_purchased = false;
            """;
    public static final String SQL_QUERY_SESSION_ID_NOT_SOLD_LAST_TICKET = """
            select t.session_id
            from ticket t
                inner join place p on t.place_id = p.id
            where t.is_purchased = false
            order by t.id, t.session_id, t.place_id
            limit 1;
            """;


    public static final String SESSION_NOT_FOUND = "Сеанс с таким id не найден";
    public static final String PLACE_NOT_FOUND = "Место не найдено";
    public static final String TICKET_NOT_SOLD = "Операция временно не доступна. Попробуйте повторить позднее.";
    public static final String TICKET_NOT_FOUND = "Не найден билет на сеанс %s c местом %s";

    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    @Override
    public Optional<Ticket> findById(Integer ticketId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_TICKET_BY_ID,
                    this::mapToTicket,
                    ticketId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Ticket> findAllNotSoldTickets(Integer sessionId) {
        return jdbcTemplate.query(SQL_QUERY_FIND_ALL_NOT_SOLD_TICKET, this::mapToTicket, sessionId);
    }

    @Override
    public List<Ticket> findAllSoldTickets(Integer sessionId) {
        return jdbcTemplate.query(SQL_QUERY_FIND_ALL_SOLD_TICKET, this::mapToTicket, sessionId);
    }

    @Override
    public Optional<Ticket> findTicketBySessionIdAndPlaceName(Integer sessionId, String placeName) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_TICKET_BY_SESSION_ID_AND_PLACE_NAME,
                    this::mapToTicket,
                    sessionId,
                    placeName));
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new TicketNotFoundException(
                    TICKET_NOT_FOUND.formatted(sessionId, placeName));
        }
    }

    public Ticket save(Ticket ticket) {
        Integer id = jdbcTemplate.queryForObject(
                SQL_QUERY_SAVE_TICKET,
                Integer.class,
                ticket.getSession().getId(),
                ticket.getPlace().getId(),
                ticket.isSold()
        );
        ticket.setId(id);
        return ticket;
    }

    public void soldTicketBySessionIdAndName(Ticket ticket) {
        int countRows = jdbcTemplate.update(SQL_QUERY_SOLD_TICKET_BY_SESSION_ID_AND_NAME, ticket.getId());
        if (countRows < 1) {
            throw new TicketNotSoldException(TICKET_NOT_SOLD);
        }
    }

    private Ticket mapToTicket(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setSold(rs.getBoolean("is_purchased"));
        if (rs.getString("session_id") != null) {
            Integer sessionId = Integer.valueOf(rs.getString("session_id"));
            ticket.setSession(sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(SESSION_NOT_FOUND)));
        }
        if (rs.getString("place_id") != null) {
            Integer placeId = Integer.valueOf(rs.getString("place_id"));
            ticket.setPlace(placeRepository.findById(placeId)
                    .orElseThrow(() -> new PlaceNotFoundException(PLACE_NOT_FOUND)));
        }
        return ticket;
    }
}
