package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Session;
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
public class SessionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final MovieRepository movieRepository;

    public Optional<Session> findById(Integer id) {
        String sql = "select * from session where id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToSession, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Session> findAll() {
        String sql = "select * from session;";
        List<Session> result = jdbcTemplate.query(sql, this::mapToSession);
        return result;
    }

    public Session save(Session session) {
        String sql = "insert into session (date_time, price, movie_id) values(?, ?, ?) returning id;";
        Integer id = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                session.getDateTime(),
                session.getPrice(),
                session.getMovie().getId()
        );
        session.setId(id);
        return session;
    }

    public Session mapToSession(ResultSet rs, int rowNum) throws SQLException {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        session.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        session.setPrice(rs.getBigDecimal("price"));
        if (rs.getString("movie_id") != null) {
            Integer movieId = Integer.valueOf(rs.getString("movie_id"));
            session.setMovie(movieRepository.findById(movieId).orElse(null));
        }
        return session;
    }
}
