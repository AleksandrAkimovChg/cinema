package com.javaacademy.cinema.service.ticket;

import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.TicketAlreadySoldException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.mapper.TicketMapper;
import com.javaacademy.cinema.repository.place.PlaceRepository;
import com.javaacademy.cinema.repository.ticket.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javaacademy.cinema.repository.ticket.TicketRepositoryImpl.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    public static final String TICKET_ALREADY_SOLD = "Билет c №%s на сеанс №%s с номером места №%s уже продан";

    private final PlaceRepository placeRepository;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public List<Ticket> getNotSoldTicketsOnSession(Integer sessionId) {
        return ticketRepository.findAllNotSoldTickets(sessionId);
    }

    @Override
    public List<Ticket> createTicketsOnSession(Session session) {
        return placeRepository.findAll().stream()
                .map(place -> new Ticket(session, place)).toList();
    }

    @Override
    public BookingDtoRs purchaseTicket(BookingDtoRq dto) {
        Ticket ticket = ticketRepository.findTicketBySessionIdAndPlaceName(dto.getSessionId(), dto.getPlace())
                .orElseThrow(() -> new TicketNotFoundException(
                        TICKET_NOT_FOUND.formatted(dto.getSessionId(), dto.getPlace())));
        if (ticket.isSold()) {
            throw new TicketAlreadySoldException(
                    TICKET_ALREADY_SOLD.formatted(ticket.getId(), dto.getSessionId(), dto.getPlace()));
        }
        ticketRepository.soldTicketBySessionIdAndName(ticket);
        return ticketMapper.convertToBookingDtoRs(ticket);
    }

    @Override
    public List<TicketAdminDto> getSoldTicketsOnSession(Integer sessionId) {
        return ticketMapper.convertToTicketDtos(ticketRepository.findAllSoldTickets(sessionId));
    }
}
