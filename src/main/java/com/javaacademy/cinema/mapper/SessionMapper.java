package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.exception.MovieNotFoundException;
import com.javaacademy.cinema.exception.SessionDateTimeInvalidFormatException;
import com.javaacademy.cinema.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.javaacademy.cinema.repository.session.SessionRepositoryImpl.FILM_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SessionMapper {
    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final String INVALID_DATE_TIME_FORMAT = "Указан неверный формат даты и "
            + "времени сеанса: %s. Формат: %s";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private final MovieRepository movieRepository;

    public Session convertToSession(SessionAdminDto dto) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dto.getDateTime(), formatter);
            return new Session(
                    null,
                    dateTime,
                    dto.getPrice(),
                    movieRepository.findById(dto.getMovieId())
                            .orElseThrow(() -> new MovieNotFoundException(FILM_NOT_FOUND))
            );
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
}
