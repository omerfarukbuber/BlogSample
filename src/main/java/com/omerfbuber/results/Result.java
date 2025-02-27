package com.omerfbuber.results;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

public class Result<TValue> implements Serializable {

    private final boolean isSuccess;
    @Getter
    private Error error;
    @Getter
    private final List<Error> validationErrors;
    private final TValue value;
    @Getter
    private final String createdUri;

    public boolean isSuccess() {
        return isSuccess;
    }
    public boolean isFailure() {
        return !isSuccess;
    }
    public TValue getValue() {
        if (isSuccess) {
            return value;
        }
        throw new IllegalStateException("The value of a failure result can't be accessed.");
    }

    private Result(TValue value, boolean isSuccess, Error error, String createdUri) {
        if ((isSuccess && error != Error.NONE)
                || (!isSuccess && error == Error.NONE)) {
            throw new IllegalArgumentException("Invalid error");
        }
        this.isSuccess = isSuccess;
        this.error = error;
        this.validationErrors = null;
        this.value = value;
        this.createdUri = createdUri;
    }

    private Result(TValue value, boolean isSuccess, List<Error> validationErrors, String createdUri) {
        if ((isSuccess && (validationErrors != null || !validationErrors.isEmpty()))
        || (!isSuccess && (validationErrors == null || validationErrors.isEmpty()))) {
            throw new IllegalArgumentException("Invalid error");
        }
        this.isSuccess = isSuccess;
        this.error = null;
        this.value = value;
        this.validationErrors = validationErrors;
        this.createdUri = createdUri;
    }

    public static <TValue> Result<TValue> success() {
        return new Result<>(null, true, Error.NONE, "");
    }

    public static <TValue> Result<TValue> success(TValue value){
        return new Result<>(value, true, Error.NONE, "");
    }

    public static <TValue> Result<TValue> created(TValue value, String createdUri){
        return new Result<>(value, true, Error.NONE, createdUri);
    }

    public static <TValue> Result<TValue> failure(Error error){
        return new Result<>(null, false, error, "");
    }

    public static <TValue> Result<TValue> validationFailure(List<Error> errors){
        Result<TValue> result = new Result<>(null, false, errors, "");
        result.error = Error.validation("Validation.General", "One or more validation errors occurred");
        return result;
    }

    public static <TValue> Result<TValue> from(TValue value){
        if (value == null){
            return failure(Error.NULL_VALUE);
        }
        return success(value);
    }
}
