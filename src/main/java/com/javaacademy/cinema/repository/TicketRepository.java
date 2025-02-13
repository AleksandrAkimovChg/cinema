package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.PlaceNotFoundException;
import com.javaacademy.cinema.exception.SessionNotFoundException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.exception.TicketNotSoldException;
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
public class TicketRepository {
    public static final String SESSION_NOT_FOUND = "Сеанс с таким id не найден";
    public static final String PLACE_NOT_FOUND = "Место не найдено";
    public static final String TICKET_NOT_SOLD = "Операция временно не доступна. Попробуйте повторить позднее.";
    public static final String TICKET_NOT_FOUND = "Не найден билет на сеанс %s c местом %s";

    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    public Optional<Ticket> findById(Integer ticketId) {
        String sql = """
                select *
                from ticket
                where id = ?;
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToTicket, ticketId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Ticket> findAllNotSoldTicket(Integer sessionId) {
        String sql = """
                select *
                from ticket
                where session_id = ? and is_purchased = false;
                """;
        return jdbcTemplate.query(sql, this::mapToTicket, sessionId);
    }

    public List<Ticket> findAllSoldTickets(Integer sessionId) {
        String sql = """
                select *
                from ticket
                where session_id = ? and is_purchased = true;
                """;
        return jdbcTemplate.query(sql, this::mapToTicket, sessionId);
    }

    public Optional<Ticket> findTicketBySessionIdAndPlaceName(Integer sessionId, String placeName) {
        String sql = """
                select t.*
                from ticket t
                    inner join place p on t.place_id = p.id
                where t.session_id = ? and p.name = ?;
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToTicket, sessionId, placeName));
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new TicketNotFoundException(
                    TICKET_NOT_FOUND.formatted(sessionId, placeName));
        }
    }

    public Ticket save(Ticket ticket) {
        String sql = """
                insert into ticket (session_id, place_id, is_purchased)
                values(?, ?, ?) returning id;
                """;
        Integer id = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                ticket.getSession().getId(),
                ticket.getPlace().getId(),
                ticket.isSold()
        );
        ticket.setId(id);
        return ticket;
    }

    public void soldBySessionIdAndName(Ticket ticket) {
        String sql = """
                update ticket
                set is_purchased = true
                where id = ?;
                """;
        int countRows = jdbcTemplate.update(sql, ticket.getId());
        if (countRows < 1) {
            throw new TicketNotSoldException(TICKET_NOT_SOLD);
        }
    }

    public Ticket mapToTicket(ResultSet rs, int rowNum) throws SQLException {
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
