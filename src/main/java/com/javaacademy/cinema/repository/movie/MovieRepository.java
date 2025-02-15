package com.javaacademy.cinema.repository.movie;

import com.javaacademy.cinema.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findById(Integer movieId);

    List<Movie> findAll();

    Movie save(Movie movie);
}
