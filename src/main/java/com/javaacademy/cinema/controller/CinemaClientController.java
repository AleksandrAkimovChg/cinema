package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.service.CinemaClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CinemaClientController {
    private final CinemaClientService cinemaClientService;

    @GetMapping("/movie")
    public List<MovieDto> getAllMovies() {
        return cinemaClientService.findAllMovies();
    }

    @GetMapping("/session")
    public List<SessionDto> getAllSessions() {
        return cinemaClientService.findAllSessions();
    }

    @GetMapping("/session/{sessionId}/free-place")
    public List<String> getFreePlaces(@PathVariable Integer sessionId) {
        return cinemaClientService.findFreePlacesOnSession(sessionId);
    }

    @PostMapping("/ticket/booking")
    public BookingDtoRs purchaseTicket(@RequestBody BookingDtoRq dto) {
        return cinemaClientService.purchaseTicket(dto);
    }
}
