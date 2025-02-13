package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.service.CinemaService;
import com.javaacademy.cinema.service.SecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@Tag(name = "Cinema admin controller",
        description = "API для управления кинотеатром администрацией: внесение фильмов, создание сеансов и просмотр "
                + "списка купленных билетов на сеанс. Для контроля доступа к ресурсу используется "
                + "специальный заголовок и токен.")
@SecurityScheme(
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.APIKEY,
        name = "user-token",
        description = "Ключ API для доступа к ресурсу администрации кинотеатра"
)
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CinemaAdminController {

    private final CinemaService cinemaService;
    private final SecurityHelper securityHelper;

    @SecurityRequirement(name = "user-token")
    @Operation(summary = "Создание фильма в кинотеатре.",
            description = "Администратор системы может добавить информацию о фильме.")
    @ApiResponse(
            responseCode = "201",
            description = "Успешное внесение информации о фильме.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MovieAdminDto.class))
            }
    )
    @ApiResponse(
            responseCode = "403",
            description = "Не указан или неверно указан специальный заголовок и токен.",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @PostMapping("/movie")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieAdminDto createMovie(
            @RequestHeader Map<String, String> headers,
            @RequestBody MovieAdminDto dto) {
        securityHelper.checkSecurityToken(headers);
        return cinemaService.createMovie(dto);
    }

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
    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSession(
            @RequestHeader Map<String, String> headers,
            @RequestBody SessionAdminDto dto) {
        securityHelper.checkSecurityToken(headers);
        cinemaService.createSession(dto);
    }

    @SecurityRequirement(name = "user-token")
    @Operation(summary = "Информация о проданных билетах на сеанс показа фильма в кинотеатре.",
            description = "Администратор может получить информацию о всех проданных местах на конкретный сеанс.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение проданных мест на выбранный сеанс.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TicketAdminDto.class)))
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
    @GetMapping("/ticket/sold")
    public List<TicketAdminDto> getSoldTicketsOnSession(
            @RequestHeader Map<String, String> headers,
            @Parameter(description = "Номер сеанса", example = "5")
            @RequestParam("session") Integer sessionId) {
        securityHelper.checkSecurityToken(headers);
        return cinemaService.getSoldTicketsOnSession(sessionId);
    }
}
