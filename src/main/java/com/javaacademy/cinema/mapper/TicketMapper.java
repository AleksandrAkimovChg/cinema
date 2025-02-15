package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketMapper {
    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public TicketAdminDto convertToTicketDto(Ticket ticket) {
        return new TicketAdminDto(
                ticket.getId(),
                ticket.getSession().getId(),
                ticket.getPlace().getName());
    }

    public List<TicketAdminDto> convertToTicketDtos(List<Ticket> ticket) {
        return ticket.stream().sorted(Comparator.comparing(Ticket::getId)).map(this::convertToTicketDto).toList();
    }

    public BookingDtoRs convertToBookingDtoRs(Ticket ticket) {
        return new BookingDtoRs(
                ticket.getId(),
                ticket.getPlace().getName(),
                ticket.getSession().getMovie().getName(),
                ticket.getSession().getDateTime().format(formatter));
    }
}
