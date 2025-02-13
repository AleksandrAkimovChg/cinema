package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.TicketAlreadySoldException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.mapper.CinemaMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javaacademy.cinema.repository.TicketRepository.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CinemaService {
    public static final String TICKET_ALREADY_SOLD = "Билет c №%s на сеанс №%s с номером места №%s уже продан";

    private final CinemaMapper cinemaMapper;
    private final MovieRepository movieRepository;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;
    private final TicketRepository ticketRepository;

    public MovieAdminDto createMovie(MovieAdminDto dto) {
        Movie movie = cinemaMapper.convertToMovie(dto);
        Movie savedMovie = movieRepository.save(movie);
        return cinemaMapper.convertToMovieAdminDto(savedMovie);
    }

    public List<MovieDto> findAllMovies() {
        return cinemaMapper.convertToMovieDto(movieRepository.findAll());
    }

    public void createSession(SessionAdminDto dto) {
        Session session = saveSession(dto);
        List<Ticket> ticket = placeRepository.findAll().stream()
                .map(place -> new Ticket(session, place)).toList();
        ticket.forEach(ticketRepository::save);
    }

    private Session saveSession(SessionAdminDto dto) {
        return sessionRepository.save(cinemaMapper.convertToSession(dto));
    }

    public List<SessionDto> findAllSessions() {
        return cinemaMapper.convertToSessionDto(sessionRepository.findAll());
    }

    public List<String> findFreePlacesOnSession(Integer sessionId) {
        List<Place> placeList = ticketRepository.findAllNotSoldTicket(sessionId).stream()
                .map(Ticket::getPlace).toList();
        return cinemaMapper.convertToNamePlaces(placeList);
    }

    public BookingDtoRs purchaseTicket(BookingDtoRq dto) {
        Ticket ticket = ticketRepository.findTicketBySessionIdAndPlaceName(dto.getSessionId(), dto.getPlace())
                .orElseThrow(() -> new TicketNotFoundException(
                        TICKET_NOT_FOUND.formatted(dto.getSessionId(), dto.getPlace())));
        if (ticket.isSold()) {
            throw new TicketAlreadySoldException(
                    TICKET_ALREADY_SOLD.formatted(ticket.getId(), dto.getSessionId(), dto.getPlace()));
        }
        ticketRepository.soldBySessionIdAndName(ticket);
        return cinemaMapper.convertToBookingDtoRs(ticket);
    }

    public List<TicketAdminDto> getSoldTicketsOnSession(Integer id) {
        return cinemaMapper.convertToTicketDtos(ticketRepository.findAllSoldTickets(id));
    }
}
