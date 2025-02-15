package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.service.authorization.AuthorizationService;
import com.javaacademy.cinema.service.place.PlaceService;
import com.javaacademy.cinema.service.session.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {
    private final AuthorizationService authorizationService;
    private final SessionService sessionService;
    private final PlaceService placeService;

    @Tag(name = "Cinema admin controller")
    @SecurityRequirement(name = "user-token")
    @Operation(summary = "Создание сеанса показа фильма в кинотеатре.",
            description = "Администратор системы  может создать новый сеанс в кинотеатре.")
    @ApiResponse(
            responseCode = "201",
            description = "Успешное создание сеанса на просмотр фильма в кинотеатре."
    )
    @ApiResponse(
            responseCode = "400",
            description = "Не неверный формат даты и времени сеанса",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "403",
            description = "Не указан или неверно указан специальный заголовок и токен",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createSession(
            @RequestHeader Map<String, String> headers,
            @RequestBody SessionAdminDto dto) {
        authorizationService.checkSecurity(headers);
        sessionService.createSession(dto);
    }

    @Tag(name = "Cinema controller")
    @Operation(summary = "Сеансы показа фильмов в кинотеатре.",
            description = "Пользователь системы может получить информацию о всех сеансах в кинотеатре.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение всех фильмов.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SessionDto.class)))
            }
    )
    @GetMapping
    public List<SessionDto> getAllSessions() {
        return sessionService.findAllSessions();
    }

    @Tag(name = "Cinema controller")
    @Operation(summary = "Свободные места на сеанс показа фильма в кинотеатре.",
            description = "Пользователь может получить информацию о свободных местах на сеанс в кинотеатре.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение свободных мест на выбранный сеанс.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
            }
    )
    @GetMapping("/{sessionId}/free-place")
    public List<String> getFreePlaces(
            @Parameter(description = "Номер сеанса", example = "5")
            @PathVariable Integer sessionId) {
        return placeService.findFreePlacesOnSession(sessionId);
    }
}
