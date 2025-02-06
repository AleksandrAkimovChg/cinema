package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.MovieNotFoundException;
import com.javaacademy.cinema.mapper.CinemaAdminMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javaacademy.cinema.repository.SessionRepository.FILM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CinemaAdminService {
    private final CinemaAdminMapper cinemaAdminMapper;
    private final MovieRepository movieRepository;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;
    private final TicketRepository ticketRepository;

    public MovieAdminDto createMovie(MovieAdminDto dto) {
        Movie movie = cinemaAdminMapper.convertToMovie(dto);
        Movie savedMovie = movieRepository.save(movie);
        return cinemaAdminMapper.convertToMovieDto(savedMovie);
    }

    public void createSession(SessionAdminDto dto) {
        Session session = saveSession(dto);
        List<Ticket> ticket = placeRepository.findAll().stream()
                .map(place -> new Ticket(session, place)).toList();
        ticket.forEach(ticketRepository::save);
    }

    private Session saveSession(SessionAdminDto dto) {
        Movie movie = movieRepository.findById(dto.getMovie())
                    .orElseThrow(() -> new MovieNotFoundException(FILM_NOT_FOUND));
        Session session = cinemaAdminMapper.convertToSession(dto);
        session.setMovie(movie);
        return sessionRepository.save(session);
    }

    public List<TicketAdminDto> getSoldTicketsOnSession(Integer id) {
        return cinemaAdminMapper.convertToTicketDtos(ticketRepository.findAllSoldTickets(id));
    }
}
