package com.javaacademy.cinema.service.session;

import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.mapper.SessionMapper;
import com.javaacademy.cinema.repository.session.SessionRepository;
import com.javaacademy.cinema.repository.ticket.TicketRepository;
import com.javaacademy.cinema.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    public void createSession(SessionAdminDto dto) {
        Session session = sessionRepository.save(sessionMapper.convertToSession(dto));
        List<Ticket> ticket = ticketService.createTicketsOnSession(session);
        ticket.forEach(ticketRepository::save);
    }

    @Override
    public List<SessionDto> findAllSessions() {
        return sessionMapper.convertToSessionDto(sessionRepository.findAll());
    }
}
