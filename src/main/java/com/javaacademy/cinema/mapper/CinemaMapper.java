package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.MovieNotFoundException;
import com.javaacademy.cinema.exception.SessionDateTimeInvalidFormatException;
import com.javaacademy.cinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.javaacademy.cinema.repository.SessionRepository.FILM_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CinemaMapper {
    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final String INVALID_DATE_TIME_FORMAT = "Указан неверный формат даты и "
            + "времени сеанса: %s. Формат: %s";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final MovieRepository movieRepository;

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

    public Session convertToSession(SessionAdminDto dto) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dto.getDateTime(), formatter);
            Session session = new Session(
                    null,
                    dateTime,
                    dto.getPrice(),
                    movieRepository.findById(dto.getMovieId())
                            .orElseThrow(() -> new MovieNotFoundException(FILM_NOT_FOUND))
            );
            return session;
        } catch (RuntimeException ex) {
            throw new SessionDateTimeInvalidFormatException(
                    INVALID_DATE_TIME_FORMAT.formatted(dto.getDateTime(), DATE_TIME_PATTERN));
        }
    }

    public SessionDto convertToSessionDto(Session session) {
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

    public TicketAdminDto convertToTicketDto(Ticket ticket) {
        return new TicketAdminDto(
                ticket.getId(),
                ticket.getSession().getId(),
                ticket.getPlace().getName(),
                ticket.isSold());
    }

    public List<TicketAdminDto> convertToTicketDtos(List<Ticket> ticket) {
        return ticket.stream().map(this::convertToTicketDto).toList();
    }

    public BookingDtoRs convertToBookingDtoRs(Ticket ticket) {
        return new BookingDtoRs(
                ticket.getId(),
                ticket.getPlace().getName(),
                ticket.getSession().getMovie().getName(),
                ticket.getSession().getDateTime().format(formatter));
    }
}
