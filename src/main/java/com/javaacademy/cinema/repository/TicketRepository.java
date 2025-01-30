package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.TicketNotSoldException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
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
    public static final String TICKET_NOT_FOUND = "Билет не найден";

    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    public Optional<Ticket> findById(Integer id) {
        String sql = "select * from ticket where id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToTicket, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Ticket> findAllNotSoldTicket(Integer id) {
        String sql = "select * from ticket where session_id = ? and is_purchased = false;";
        List<Ticket> result = jdbcTemplate.query(sql, this::mapToTicket, id);
        return result;
    }

    public List<Ticket> findAllSoldTickets(Integer id) {
        String sql = "select * from ticket where session_id = ? and is_purchased = true;";
        List<Ticket> result = jdbcTemplate.query(sql, this::mapToTicket, id);
        return result;
    }

    public Ticket save(Ticket ticket) {
        String sql = "insert into ticket (session_id, place_id, is_purchased) values(?, ?, ?) returning id;";
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

    public void sold(Integer id) {
        Ticket ticket = findById(id).orElseThrow(() -> new TicketNotFoundException(TICKET_NOT_FOUND));
        String sql = "update ticket set is_purchased = true where id = ?;";
        int countRows = jdbcTemplate.update(sql, ticket.getId());
        if (countRows < 1) {
            throw new RuntimeException();
        }
    }

    public Ticket mapToTicket(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setSold(rs.getBoolean("is_purchased"));
        if (rs.getString("session_id") != null) {
            Integer sessionId = Integer.valueOf(rs.getString("session_id"));
            ticket.setSession(sessionRepository.findById(sessionId).orElse(null));
        }
        if (rs.getString("place_id") != null) {
            Integer placeId = Integer.valueOf(rs.getString("place_id"));
            ticket.setPlace(placeRepository.findById(placeId).orElse(null));
        }
        return ticket;
    }
}
