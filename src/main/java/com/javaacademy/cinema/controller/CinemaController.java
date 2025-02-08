package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.service.CinemaService;
import com.javaacademy.cinema.service.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;
    private final SecurityHelper securityHelper;

    @GetMapping("/movie")
    public List<MovieDto> getAllMovies() {
        return cinemaService.findAllMovies();
    }

    @PostMapping("/movie")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieAdminDto createMovie(
            @RequestHeader Map<String, String> headers,
            @RequestBody MovieAdminDto dto) {
        securityHelper.checkSecurityToken(headers);
        return cinemaService.createMovie(dto);
    }

    @GetMapping("/session")
    public List<SessionDto> getAllSessions() {
        return cinemaService.findAllSessions();
    }

    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSession(
            @RequestHeader Map<String, String> headers,
            @RequestBody SessionAdminDto dto) {
        securityHelper.checkSecurityToken(headers);
        cinemaService.createSession(dto);
    }

    @GetMapping("/session/{sessionId}/free-place")
    public List<String> getFreePlaces(@PathVariable Integer sessionId) {
        return cinemaService.findFreePlacesOnSession(sessionId);
    }

    @PostMapping("/ticket/booking")
    public BookingDtoRs purchaseTicket(@RequestBody BookingDtoRq dto) {
        return cinemaService.purchaseTicket(dto);
    }

    @GetMapping("/ticket/sold")
    public List<TicketAdminDto> getSoldTicketsOnSession(
            @RequestHeader Map<String, String> headers,
            @RequestParam("session") Integer sessionId) {
        securityHelper.checkSecurityToken(headers);
        return cinemaService.getSoldTicketsOnSession(sessionId);
    }
}
