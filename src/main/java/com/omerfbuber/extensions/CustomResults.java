package com.omerfbuber.extensions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import com.omerfbuber.results.Result;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CustomResults {
    public static ProblemDetail toProblemDetail(Result result) {
        if (result.isSuccess()) {
            throw new IllegalStateException("The result is successful.");
        }

        var problemDetail = ProblemDetail.forStatus(getStatusCode(result.getError()));
        problemDetail.setType(getType(result.getError()));
        problemDetail.setDetail(getDetail(result.getError()));
        problemDetail.setTitle(getTitle(result.getError()));
        problemDetail.setProperties(getErrors(result));

        return problemDetail;
    }

    private static String getTitle(com.omerfbuber.results.Error error){
        return switch (error.type()){
            case VALIDATION, CONFLICT, FAILURE, PROBLEM, NOT_FOUND -> error.code();
            default -> "Server.Failure";
        };
    }

    private static String getDetail(com.omerfbuber.results.Error error){
        return switch (error.type()){
            case VALIDATION, CONFLICT, FAILURE, PROBLEM, NOT_FOUND -> error.description();
            default -> "An unexpected error occurred.";
        };
    }

    private static URI getType(com.omerfbuber.results.Error error){
        var uri = switch (error.type()){
            case VALIDATION, PROBLEM, FAILURE -> "https://tools.ietf.org/html/rfc7231#section-6.5.1";
            case NOT_FOUND -> "https://tools.ietf.org/html/rfc7231#section-6.5.4";
            case CONFLICT -> "https://tools.ietf.org/html/rfc7231#section-6.5.8";
            default -> "https://tools.ietf.org/html/rfc7231#section-6.6.1";
        };
        return URI.create(uri);
    }

    private static HttpStatus getStatusCode(com.omerfbuber.results.Error error){
        return switch (error.type()){
            case VALIDATION, PROBLEM, FAILURE -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private static Map<String, Object> getErrors(Result result){
        if (result.getValidationErrors() == null || result.getValidationErrors().isEmpty()){
            return null;
        }

        Map<String, Object> errors = new HashMap<>();
        errors.put("errors", result.getValidationErrors());
        return errors;
    }
}
