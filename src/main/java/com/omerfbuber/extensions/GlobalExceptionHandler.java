package com.omerfbuber.extensions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle(ex.getClass().getSimpleName());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.1"));

        log.error("An error occurred: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return ResponseEntity.of(problemDetail).build();
    }

}
