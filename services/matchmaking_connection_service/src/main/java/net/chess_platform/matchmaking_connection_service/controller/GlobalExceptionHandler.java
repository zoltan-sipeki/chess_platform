package net.chess_platform.matchmaking_connection_service.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.matchmaking_connection_service.dto.response.ErrorResponse;
import net.chess_platform.matchmaking_connection_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_connection_service.exception.ServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MatchmakingException e, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI(),
                OffsetDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleException(ServiceUnavailableException e, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage(),
                request.getRequestURI(), OffsetDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException e, HttpServletRequest request) {
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
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), request.getRequestURI(),
                OffsetDateTime.now());
    }
}
