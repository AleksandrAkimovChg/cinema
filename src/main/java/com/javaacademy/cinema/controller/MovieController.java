package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.dto.admin.MovieAdminDto;
import com.javaacademy.cinema.dto.client.MovieDto;
import com.javaacademy.cinema.service.authorization.AuthorizationService;
import com.javaacademy.cinema.service.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieController {
    private final AuthorizationService authorizationService;
    private final MovieService movieService;

    @Tag(name = "Cinema admin controller")
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
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MovieAdminDto createMovie(
            @RequestHeader Map<String, String> headers,
            @RequestBody MovieAdminDto dto) {
        authorizationService.checkSecurity(headers);
        return movieService.createMovie(dto);
    }

    @Tag(name = "Cinema controller")
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
    @GetMapping()
    public List<MovieDto> getAllMovies() {
        return movieService.findAllMovies();
    }
}
