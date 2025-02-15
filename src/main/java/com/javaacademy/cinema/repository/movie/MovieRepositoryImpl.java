package com.javaacademy.cinema.repository.movie;


import com.javaacademy.cinema.entity.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepository {
    private static final String SQL_QUERY_FIND_MOVIE_BY_ID = """
            select *
            from movie
            where id = ?;
            """;
    private static final String SQL_QUERY_FIND_ALL_MOVIES = """
            select *
            from movie;
            """;
    private static final String SQL_QUERY_SAVE_MOVIE = """
            insert into movie (name, description)
            values(?, ?) returning id;
            """;
    public static final String SQL_QUERY_LAST_MOVIE_ID = """
            select id
            from movie
            order by id desc limit 1;
            """;
    public static final String SQL_QUERY_COUNT_ALL_MOVIES = """
            select count(*)
            from movie;
            """;
    public static final String SQL_QUERY_CURRENT_SEQ_MOVIE_ID = """
            select currval('movie_id_seq');
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Movie> findById(Integer movieId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_MOVIE_BY_ID,
                    this::mapToMovie,
                    movieId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Movie> findAll() {
        return jdbcTemplate.query(SQL_QUERY_FIND_ALL_MOVIES, this::mapToMovie);
    }

    @Override
    public Movie save(Movie movie) {

        Integer id = jdbcTemplate.queryForObject(
                SQL_QUERY_SAVE_MOVIE,
                Integer.class,
                movie.getName(),
                movie.getDescription());
        movie.setId(id);
        return movie;
    }

    private Movie mapToMovie(ResultSet rs, int rowNum) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setName(rs.getString("name"));
        movie.setDescription(rs.getString("description"));
        return movie;
    }
}
