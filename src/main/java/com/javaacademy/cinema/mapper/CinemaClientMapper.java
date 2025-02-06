package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CinemaClientMapper {

    public MovieDto convertToMovieDto(Movie movie) {
        return new MovieDto(movie.getName(), movie.getDescription());
    }

    public List<MovieDto> convertToMovieDto(List<Movie> movie) {
        return movie.stream().map(this::convertToMovieDto).toList();
    }

    public SessionDto convertToSessionDto(Session session) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return new SessionDto(
                session.getId(),
                session.getMovie().getName(),
                session.getDateTime().format(formatter),
                session.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    public List<SessionDto> convertToSessionDto(List<Session> movie) {
        return movie.stream().map(this::convertToSessionDto).toList();
    }

    public String convertToNamePlace(Place place) {
        return place.getName();
    }

    public List<String> convertToNamePlaces(List<Place> place) {
        return place.stream().map(this::convertToNamePlace).toList();
    }

    public BookingDtoRs convertToBookingDtoRs(Ticket ticket) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return new BookingDtoRs(
                ticket.getId(),
                ticket.getPlace().getName(),
                ticket.getSession().getMovie().getName(),
                ticket.getSession().getDateTime().format(formatter));
    }
}
