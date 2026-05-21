package net.chess_platform.user_service.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.user_service.dto.ErrorDto;
import net.chess_platform.user_service.exception.EntityNotFoundException;
import net.chess_platform.user_service.exception.InvalidImageException;
import net.chess_platform.user_service.exception.InvalidUserException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ InvalidImageException.class, InvalidUserException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(Exception e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), getErrorMessage(e, HttpStatus.BAD_REQUEST),
                OffsetDateTime.now(),
                request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(EntityNotFoundException e, HttpServletRequest request) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), getErrorMessage(e, HttpStatus.NOT_FOUND),
                OffsetDateTime.now(),
                request.getRequestURI());
    }

    private String getErrorMessage(Exception e, HttpStatus status) {
        var message = e.getMessage();
        return StringUtils.hasText(message) ? message : status.getReasonPhrase();
    }
}
