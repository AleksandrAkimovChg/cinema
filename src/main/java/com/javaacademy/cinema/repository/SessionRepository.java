package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.exception.MovieNotFoundException;
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
    public static final String FILM_NOT_FOUND = "Фильм с таким id не найден";
    private final JdbcTemplate jdbcTemplate;
    private final MovieRepository movieRepository;

    public Optional<Session> findById(Integer sessionId) {
        String sql = "select * from session where id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToSession, sessionId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Session> findAll() {
        String sql = "select * from session;";
        return jdbcTemplate.query(sql, this::mapToSession);
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
            session.setMovie(movieRepository.findById(movieId)
                    .orElseThrow(() -> new MovieNotFoundException(FILM_NOT_FOUND)));
        }
        return session;
    }
}
