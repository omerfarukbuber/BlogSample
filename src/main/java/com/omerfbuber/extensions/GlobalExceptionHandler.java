package com.omerfbuber.extensions;

import com.omerfbuber.results.Error;
import com.omerfbuber.results.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<Error> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error ->
                    errors.add(Error.validation(error.getField(), error.getDefaultMessage())));

        return ResponseEntity.of(CustomResults.toProblemDetail(Result.validationFailure(errors))).build();
    }
}
