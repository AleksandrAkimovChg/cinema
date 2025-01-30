package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.TicketAlreadySoldException;
import com.javaacademy.cinema.exception.TicketNotSoldException;
import com.javaacademy.cinema.mapper.CinemaClientMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CinemaClientService {
    public static final String TICKET_ALREADY_SOLD = "Билет с № %s уже продан";
    public static final String TICKET_NOT_SOLD = "Операция временно не доступна. Попробуйте повторить позднее.";

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

    public List<String> findAllFreePlaces() {
        return cinemaClientMapper.convertToNamePlaces(placeRepository.findAll());
    }

    public BookingDtoRs purchaseTicket(BookingDtoRq dto) {
        Session session = sessionRepository.findById(dto.getId()).orElseThrow();
        Ticket ticket = ticketRepository.findAllNotSoldTicket(session.getId()).stream()
                .filter(e -> Objects.equals(dto.getPlace(), e.getPlace().getName()))
                .findFirst().orElseThrow();
        if (ticket.isSold()) {
            throw new TicketAlreadySoldException(TICKET_ALREADY_SOLD.formatted(ticket.getId()));
        }
        try {
            ticketRepository.sold(ticket.getId());
        } catch (RuntimeException ex) {
            throw new TicketNotSoldException(TICKET_NOT_SOLD);
        }
        return cinemaClientMapper.convertToBookingDtoRs(ticket, session);
    }
}
