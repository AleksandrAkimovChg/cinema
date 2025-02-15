package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    public Movie convertToMovie(MovieAdminDto dto) {
        return new Movie(null, dto.getName(), dto.getDescription());
    }

    public MovieAdminDto convertToMovieAdminDto(Movie movie) {
        return new MovieAdminDto(movie.getId(), movie.getName(), movie.getDescription());
    }

    public MovieDto convertToMovieDto(Movie movie) {
        return new MovieDto(movie.getName(), movie.getDescription());
    }

    public List<MovieDto> convertToMovieDto(List<Movie> movie) {
        return movie.stream().map(this::convertToMovieDto).toList();
    }
}
