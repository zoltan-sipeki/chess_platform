package net.chess_platform.matchmaking_api_service.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.matchmaking_api_service.dto.ErrorDto;
import net.chess_platform.matchmaking_api_service.exception.EntityNotFoundException;
import net.chess_platform.matchmaking_api_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_api_service.exception.ServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(MatchmakingException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI(),
                OffsetDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(EntityNotFoundException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI(),
                OffsetDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDto handleException(ServiceUnavailableException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage(),
                request.getRequestURI(), OffsetDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(MethodArgumentNotValidException e, HttpServletRequest request) {
        var errorMessage = new StringBuilder();
        var errors = e.getBindingResult().getFieldErrors();
        for (int i = 0; i < errors.size(); i++) {
            var error = errors.get(i);
            var field = error.getField();
            var message = error.getDefaultMessage();

            errorMessage.append(field).append(": ").append(message);
            if (i + 1 < errors.size()) {
                errorMessage.append("; ");
            }
        }
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), request.getRequestURI(),
                OffsetDateTime.now());
    }
}
