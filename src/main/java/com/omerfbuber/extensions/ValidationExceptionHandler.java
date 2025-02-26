package com.omerfbuber.extensions;

import com.omerfbuber.results.Error;
import com.omerfbuber.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(1)
@Slf4j
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<com.omerfbuber.results.Error> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error ->
                        errors.add(Error.validation(error.getField(), error.getDefaultMessage())));

        var problemDetail = CustomResults.toProblemDetail(Result.validationFailure(errors));

        String logMessage = errors.stream()
                .map(error -> String.format("Field: %s - Message: %s", error.code(), error.description()))
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", logMessage);

        return ResponseEntity.of(problemDetail).build();
    }
}
