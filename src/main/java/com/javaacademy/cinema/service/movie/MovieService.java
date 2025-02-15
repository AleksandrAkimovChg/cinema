package com.javaacademy.cinema.service.movie;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.client.MovieDto;

import java.util.List;

public interface MovieService {

    MovieAdminDto createMovie(MovieAdminDto dto);

    List<MovieDto> findAllMovies();
}
