package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.config.CinemaSecurityProperty;
import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.exception.SecretTokenCheckFailedException;
import com.javaacademy.cinema.service.CinemaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class CinemaAdminController {
    public static final String SECRET_TOKEN_CHECK_FAILED = "Нет прав на операцию. Обратитесь в службу поддержки.";

    private final CinemaAdminService cinemaAdminService;
    private final CinemaSecurityProperty cinemaSecurityProperty;

    @PostMapping("/movie")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieAdminDto createMovie(
            @RequestHeader Map<String, String> headers,
            @RequestBody MovieAdminDto dto) {
        checkSecurityToken(headers);
        return cinemaAdminService.createMovie(dto);
    }

    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSession(
            @RequestHeader Map<String, String> headers,
            @RequestBody SessionAdminDto dto) {
        checkSecurityToken(headers);
        cinemaAdminService.createSession(dto);
    }

    @GetMapping("/ticket/sold")
    public List<TicketAdminDto> getSoldTicketsOnSession(
            @RequestHeader Map<String, String> headers,
            @RequestParam("session") Integer sessionId) {
        checkSecurityToken(headers);
        return cinemaAdminService.getSoldTicketsOnSession(sessionId);
    }

    private void checkSecurityToken(Map<String, String> headers) {
        if (!headers.containsKey(cinemaSecurityProperty.getHeader())) {
            throw new SecretTokenCheckFailedException(SECRET_TOKEN_CHECK_FAILED);
        }
        if (!isSecurityTokenEquals(headers.get(cinemaSecurityProperty.getHeader()))) {
            throw new SecretTokenCheckFailedException(SECRET_TOKEN_CHECK_FAILED);
        }
    }

    private boolean isSecurityTokenEquals(String userToken) {
        return Objects.equals(userToken, cinemaSecurityProperty.getToken());
    }
}
