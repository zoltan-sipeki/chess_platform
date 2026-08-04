package net.chess_platform.chat_service.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.chat_service.dto.ErrorDto;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.exception.InvalidFriendRequestException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ InvalidFriendRequestException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(Exception e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), getErrorMessage(e, HttpStatus.BAD_REQUEST),
                Instant.now(),
                request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(EntityNotFoundException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), getErrorMessage(e, HttpStatus.NOT_FOUND),
                Instant.now(),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleException(AccessDeniedException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.FORBIDDEN.value(), getErrorMessage(e, HttpStatus.FORBIDDEN),
                Instant.now(),
                request.getRequestURI());
    }

    private String getErrorMessage(Exception e, HttpStatus status) {
        var message = e.getMessage();
        return StringUtils.hasText(message) ? message : status.getReasonPhrase();
    }

}
