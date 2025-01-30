package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Place;
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
public class PlaceRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Place> findById(Integer id) {
        String sql = "select * from place where id = ?;";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToPlace, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Place> findAll() {
        String sql = "select * from place;";
        List<Place> result = jdbcTemplate.query(sql, this::mapToPlace);
        return result;
    }

    public Place mapToPlace(ResultSet rs, int rowNum) throws SQLException {
        Place place = new Place();
        place.setId(rs.getInt("id"));
        place.setName(rs.getString("name"));
        return place;
    }
}
