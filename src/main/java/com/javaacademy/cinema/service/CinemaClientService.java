package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.PlaceNotFoundException;
import com.javaacademy.cinema.exception.SessionNotFoundException;
import com.javaacademy.cinema.exception.TicketAlreadySoldException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.mapper.CinemaClientMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.javaacademy.cinema.repository.TicketRepository.PLACE_NOT_FOUND;
import static com.javaacademy.cinema.repository.TicketRepository.SESSION_NOT_FOUND;
import static com.javaacademy.cinema.repository.TicketRepository.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CinemaClientService {
    public static final String TICKET_ALREADY_SOLD = "Билет с № %s уже продан";

    private final MovieRepository movieRepository;
    private final SessionRepository sessionRepository;
    private final CinemaClientMapper cinemaClientMapper;
    private final PlaceRepository placeRepository;
    private final TicketRepository ticketRepository;


    public List<MovieDto> findAllMovies() {
        return cinemaClientMapper.convertToMovieDto(movieRepository.findAll());
    }

    public List<SessionDto> findAllSessions() {
        return cinemaClientMapper.convertToSessionDto(sessionRepository.findAll());
    }

    public List<String> findFreePlacesOnSession(Integer sessionId) {
        List<Place> placeList = ticketRepository.findAllNotSoldTicket(sessionId).stream()
                .map(ticket -> ticket.getPlace()).toList();
        return cinemaClientMapper.convertToNamePlaces(placeList);
    }

    public BookingDtoRs purchaseTicket(BookingDtoRq dto) {
        Session session = sessionRepository.findById(dto.getId())
                .orElseThrow(() -> new SessionNotFoundException(SESSION_NOT_FOUND));
        Place place = placeRepository.findAll().stream()
                .filter(e -> Objects.equals(dto.getPlace(), e.getName())).findFirst()
                .orElseThrow(() -> new PlaceNotFoundException(PLACE_NOT_FOUND));
        Ticket ticket = ticketRepository.findTicketBySessionIdAndPlaceId(session.getId(), place.getId()).
                orElseThrow(() -> new TicketNotFoundException(TICKET_NOT_FOUND));
        if (ticket.isSold()) {
            throw new TicketAlreadySoldException(TICKET_ALREADY_SOLD.formatted(ticket.getId()));
        }
        ticketRepository.sold(ticket.getId());
        return cinemaClientMapper.convertToBookingDtoRs(ticket);
    }
}
