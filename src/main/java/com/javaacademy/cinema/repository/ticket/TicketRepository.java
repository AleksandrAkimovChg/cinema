package com.javaacademy.cinema.repository.ticket;

import com.javaacademy.cinema.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> findById(Integer ticketId);

    List<Ticket> findAllNotSoldTickets(Integer sessionId);

    List<Ticket> findAllSoldTickets(Integer sessionId);

    Optional<Ticket> findTicketBySessionIdAndPlaceName(Integer sessionId, String placeName);

    Ticket save(Ticket ticket);

    void soldTicketBySessionIdAndName(Ticket ticket);
}
