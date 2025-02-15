package com.javaacademy.cinema.service.ticket;

import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;

import java.util.List;

public interface TicketService {

    List<Ticket> getNotSoldTicketsOnSession(Integer sessionId);

    List<Ticket> createTicketsOnSession(Session session);

    BookingDtoRs purchaseTicket(BookingDtoRq dto);

    List<TicketAdminDto> getSoldTicketsOnSession(Integer id);
}
