package com.omerfbuber.results;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public record Error(String code, String description, @JsonIgnore ErrorType type) implements Serializable {

    public static Error NONE = new Error("", "", ErrorType.FAILURE);
    public static Error NULL_VALUE = new Error(
            "General.Null", "Null value was provided.", ErrorType.FAILURE);

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
}
