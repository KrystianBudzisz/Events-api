package com.example.eventsapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(DatabaseException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ExceptionDto handleDatabaseException(DatabaseException exception) {
            return new ExceptionDto(exception.getMessage());
        }
        @ExceptionHandler(DuplicateResourceException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ExceptionDto handleDuplicateResourceException(DuplicateResourceException exception) {
            return new ExceptionDto(exception.getMessage());
        }


}
