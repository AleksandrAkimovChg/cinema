package com.javaacademy.cinema.service.place;

import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.mapper.PlaceMapper;
import com.javaacademy.cinema.repository.ticket.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {


    private final TicketRepository ticketRepository;
    private final PlaceMapper placeMapper;

    @Override
    public List<String> findFreePlacesOnSession(Integer sessionId) {
        List<Place> placeList = ticketRepository.findAllNotSoldTickets(sessionId).stream()
                .map(Ticket::getPlace).toList();
        return placeMapper.convertToNamePlaces(placeList);
    }
}
