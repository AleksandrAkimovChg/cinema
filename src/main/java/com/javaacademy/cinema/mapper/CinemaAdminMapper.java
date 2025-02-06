package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.SessionDateTimeInvalidFormatException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CinemaAdminMapper {

    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final String INVALID_DATE_TIME_FORMAT = "Указан неверный формат даты и "
            + "времени сеанса: %s. Формат: %s";

    public Movie convertToMovie(MovieAdminDto dto) {
        return new Movie(null, dto.getName(), dto.getDescription());
    }

    public MovieAdminDto convertToMovieDto(Movie movie) {
        return new MovieAdminDto(movie.getId(), movie.getName(), movie.getDescription());
    }

    public Session convertToSession(SessionAdminDto dto) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            LocalDateTime dateTime = LocalDateTime.parse(dto.getDateTime(), formatter);
            return new Session(null, dateTime, dto.getPrice(), null);
        } catch (RuntimeException ex) {
            throw new SessionDateTimeInvalidFormatException(
                    INVALID_DATE_TIME_FORMAT.formatted(dto.getDateTime(), DATE_TIME_PATTERN));
        }
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
}
