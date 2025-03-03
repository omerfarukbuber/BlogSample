package com.omerfbuber.extension;

import com.omerfbuber.result.Result;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ResponseEntityExtension {
    public static <T> ResponseEntity<T> okOrProblem(Result<T> result){

        if (result.isFailure()){
            return ResponseEntity.of(CustomResults.toProblemDetail(result)).build();
        }
        return ResponseEntity.ok(result.getValue());
    }

    public static <T> ResponseEntity<T> noContentOrProblem(Result<T> result){
        if (result.isFailure()){
            return ResponseEntity.of(CustomResults.toProblemDetail(result)).build();
        }
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<T> createdOrProblem(Result<T> result){
        if (result.isFailure()){
            return ResponseEntity.of(CustomResults.toProblemDetail(result)).build();
        }
        if (result.getCreatedUri().isEmpty()){
            throw new IllegalArgumentException("No created URI provided");
        }
        var createUri = URI.create(result.getCreatedUri());
        return ResponseEntity.created(createUri).body(result.getValue());
    }
}
