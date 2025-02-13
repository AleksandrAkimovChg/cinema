package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.dto.client.SessionDto;
import com.javaacademy.cinema.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Cinema controller",
        description = "API для использования услуг кинотеатра зрителями: просмотр информации о фильмах и сеансах, "
                + "поиска свободных мест и покупки билета на них.")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;

    @Operation(summary = "Все фильмы в кинотеатре.",
            description = "Пользователи системы могут получить информацию о всех фильмах в кинотеатре.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение всех фильмов.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MovieDto.class)))
            }
    )
    @GetMapping("/movie")
    public List<MovieDto> getAllMovies() {
        return cinemaService.findAllMovies();
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
    @GetMapping("/session")
    public List<SessionDto> getAllSessions() {
        return cinemaService.findAllSessions();
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
    @GetMapping("/session/{sessionId}/free-place")
    public List<String> getFreePlaces(
            @Parameter(description = "Номер сеанса", example = "5")
            @PathVariable Integer sessionId) {
        return cinemaService.findFreePlacesOnSession(sessionId);
    }

    @Tag(name = "Cinema controller")
    @Operation(summary = "Покупка места на сеанс показа фильма в кинотеатре.",
            description = "Пользователь может получить информацию о свободных местах на сеанс в кинотеатре.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешная покупка билета на сеанс показа фильма в кинотеатре.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookingDtoRs.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Билет на сеанс с таким id и местом не найден.",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "409",
            description = "Билет на сеанс с таким id и местом не найден.",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "417",
            description = "Произошла внутренняя ошибка системы. "
                    + "Попробуйте повторить позднее.",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @PostMapping("/ticket/booking")
    public BookingDtoRs purchaseTicket(@RequestBody BookingDtoRq dto) {
        return cinemaService.purchaseTicket(dto);
    }
}
