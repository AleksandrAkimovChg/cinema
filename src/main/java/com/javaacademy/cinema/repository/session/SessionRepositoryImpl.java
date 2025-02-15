package com.javaacademy.cinema.repository.session;

import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.exception.MovieNotFoundException;
import com.javaacademy.cinema.repository.movie.MovieRepository;
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
public class SessionRepositoryImpl implements SessionRepository {
    private static final String SQL_QUERY_FIND_SESSION_BY_ID = """
            select *
            from session
            where id = ?;
            """;
    private static final String SQL_QUERY_FIND_ALL_SESSION = """
            select *
            from session;
            """;
    private static final String SQL_QUERY_SAVE_SESSION = """
            insert into session (date_time, price, movie_id)
            values(?, ?, ?) returning id;
            """;
    public static final String SQL_QUERY_LAST_SESSION_ID = """
            select id
            from session
            order by id desc
            limit 1;
            """;
    public static final String SQL_QUERY_COUNT_ALL_SESSIONS = """
            select count(*)
            from session;
            """;
    public static final String SQL_QUERY_SESSIONS_IS_EXISTS = """
            select count(*) > 0
            from session;
            """;
    public static final String SQL_QUERY_CURRENT_SEQ_SESSION_ID = """
            select currval('session_id_seq');
            """;
    public static final String SQL_QUERY_SESSION_IN_LAST_SOLD_TICKET = """
            select distinct session_id
            from ticket
            where is_purchased = true
            order by session_id desc
            limit 1;
            """;


    public static final String FILM_NOT_FOUND = "Фильм с таким id не найден";

    private final JdbcTemplate jdbcTemplate;
    private final MovieRepository movieRepository;

    @Override
    public Optional<Session> findById(Integer sessionId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_SESSION_BY_ID,
                    this::mapToSession,
                    sessionId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Session> findAll() {
        return jdbcTemplate.query(SQL_QUERY_FIND_ALL_SESSION, this::mapToSession);
    }

    @Override
    public Session save(Session session) {

        Integer id = jdbcTemplate.queryForObject(
                SQL_QUERY_SAVE_SESSION,
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
