package com.javaacademy.cinema.repository;


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
public class MovieRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Movie> findById(Integer id) {
        String sql = "select * from movie where id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToMovie, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Movie> findAll() {
        String sql = "select * from movie;";
        List<Movie> result = jdbcTemplate.query(sql, this::mapToMovie);
        return result;
    }

    public Movie save(Movie movie) {
        String sql = "insert into movie (name, description) values(?, ?) returning id;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, movie.getName(), movie.getDescription());
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
