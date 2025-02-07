package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.exception.MovieNotFoundException;
import com.javaacademy.cinema.exception.PlaceNotFoundException;
import com.javaacademy.cinema.exception.SecretTokenCheckFailedException;
import com.javaacademy.cinema.exception.SessionDateTimeInvalidFormatException;
import com.javaacademy.cinema.exception.SessionNotFoundException;
import com.javaacademy.cinema.exception.TicketAlreadySoldException;
import com.javaacademy.cinema.exception.TicketNotFoundException;
import com.javaacademy.cinema.exception.TicketNotSoldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(SecretTokenCheckFailedException.class)
    public ResponseEntity handlerSecurityException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(TicketAlreadySoldException.class)
    public ResponseEntity handlerTicketAlreadySoldException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(SessionDateTimeInvalidFormatException.class)
    public ResponseEntity handlerSessionDateTimeInvalidFormatException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity handlerTicketNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TicketNotSoldException.class)
    public ResponseEntity handlerTicketNotSoldException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
    }

    @ExceptionHandler({
            MovieNotFoundException.class,
            SessionNotFoundException.class,
            PlaceNotFoundException.class
    })
    public ResponseEntity handlerServerErrorException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
