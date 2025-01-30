package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.service.CinemaClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        return ResponseEntity.ok(cinemaClientService.findAllMovies());
    }

    @GetMapping("/session")
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        return ResponseEntity.ok(cinemaClientService.findAllSessions());
    }

    @GetMapping("/session/{id}/free-place")
    public ResponseEntity<List<String>> getFreePlaces() {
        return ResponseEntity.ok(cinemaClientService.findAllFreePlaces());
    }

    @PostMapping("/ticket/booking")
    public ResponseEntity<BookingDtoRs> purchaseTicket(@RequestBody BookingDtoRq dto) {
        return ResponseEntity.ok(cinemaClientService.purchaseTicket(dto));
    }
}
