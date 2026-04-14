package net.chess_platform.match_service.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.match_service.dto.ErrorResponse;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.exception.UserAlreadyInMatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(EntityNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                request.getRequestURI());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleException(UserAlreadyInMatchException e, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                request.getRequestURI());
    }
}
