package com.javaacademy.cinema.repository.place;

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
public class PlaceRepositoryImpl implements PlaceRepository {
    private static final String SQL_QUERY_FIND_PLACE_BY_ID = """
            select *
            from place
            where id = ?;
            """;
    private static final String SQL_QUERY_FIND_ALL_PLACES = """
            select *
            from place;
            """;
    public static final String SQL_QUERY_COUNT_ALL_PLACES = """
            select count(*)
            from place;
            """;
    public static final String SQL_QUERY_PLACE_NAME_NOT_SOLD_LAST_TICKET = """
             select p.name
             from ticket t
                 inner join place p on t.place_id = p.id
             where t.is_purchased = false
             order by t.id, t.session_id, t.place_id
             limit 1;
            """;


    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Place> findById(Integer placeId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_PLACE_BY_ID,
                    this::mapToPlace,
                    placeId));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Place> findAll() {
        return jdbcTemplate.query(SQL_QUERY_FIND_ALL_PLACES, this::mapToPlace);
    }

    public Place mapToPlace(ResultSet rs, int rowNum) throws SQLException {
        Place place = new Place();
        place.setId(rs.getInt("id"));
        place.setName(rs.getString("name"));
        return place;
    }
}
