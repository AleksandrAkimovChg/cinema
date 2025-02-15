package com.javaacademy.cinema.service.movie;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.mapper.MovieMapper;
import com.javaacademy.cinema.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public MovieAdminDto createMovie(MovieAdminDto dto) {
        Movie movie = movieMapper.convertToMovie(dto);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.convertToMovieAdminDto(savedMovie);
    }

    @Override
    public List<MovieDto> findAllMovies() {
        return movieMapper.convertToMovieDto(movieRepository.findAll());
    }
}
