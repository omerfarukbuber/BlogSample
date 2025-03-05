package com.omerfbuber.result;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public record Error(String code, String description, @JsonIgnore ErrorType type) implements Serializable {

    public static Error NONE = new Error("", "", ErrorType.FAILURE);
    public static Error NULL_VALUE = new Error(
            "General.Null", "Null value was provided.", ErrorType.FAILURE);
    public static Error TOO_MANY_REQUESTS = new Error(
            "General.TooManyRequests", "Too many requests.", ErrorType.TOO_MANY_REQUESTS);

    public static Error failure(String code, String description) {
        return new Error(code, description, ErrorType.FAILURE);
    }

    public static Error notFound(String code, String description) {
        return new Error(code, description, ErrorType.NOT_FOUND);
    }

    public static Error problem(String code, String description) {
        return new Error(code, description, ErrorType.PROBLEM);
    }

    public static Error conflict(String code, String description) {
        return new Error(code, description, ErrorType.CONFLICT);
    }

    public static Error validation(String fieldName, String message){
        return new Error(fieldName, message, ErrorType.VALIDATION);
    }

    public static Error unauthorized(String code, String description) {
        return new Error(code, description, ErrorType.UNAUTHORIZED);
    }

    public static Error forbidden(String code, String description) {
        return new Error(code, description, ErrorType.FORBIDDEN);
    }

    public static Error tooManyRequests(String code, String description) {
        return new Error(code, description, ErrorType.TOO_MANY_REQUESTS);
    }

    public static Error tooManyRequests(long requestPerMinute) {
        return new Error("Too many requests.",
                "You only allow " + requestPerMinute + " requests per minute.", ErrorType.TOO_MANY_REQUESTS);
    }
}
