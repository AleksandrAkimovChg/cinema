package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.admin.TicketAdminDto;
import com.javaacademy.cinema.dto.client.BookingDtoRq;
import com.javaacademy.cinema.dto.client.BookingDtoRs;
import com.javaacademy.cinema.service.authorization.AuthorizationService;
import com.javaacademy.cinema.service.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final AuthorizationService authorizationService;
    private final TicketService ticketService;

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
    @PostMapping("/booking")
    public BookingDtoRs purchaseTicket(@RequestBody BookingDtoRq dto) {
        return ticketService.purchaseTicket(dto);
    }

    @Tag(name = "Cinema admin controller")
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
    @GetMapping("/sold")
    public List<TicketAdminDto> getSoldTicketsOnSession(
            @RequestHeader Map<String, String> headers,
            @Parameter(description = "Номер сеанса", example = "5")
            @RequestParam("session") Integer sessionId) {
        authorizationService.checkSecurity(headers);
        return ticketService.getSoldTicketsOnSession(sessionId);
    }
}
