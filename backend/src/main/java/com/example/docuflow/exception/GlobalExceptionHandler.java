// src/main/java/com/example/docuflow/exception/GlobalExceptionHandler.java
package com.example.docuflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
        // You can special‑case your other custom exceptions here
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now(),
                "message", ex.getMessage()
        );
        // default to 500 if no @ResponseStatus on exception
        HttpStatus status = ex.getClass().getAnnotation(ResponseStatus.class) != null
                ? ex.getClass().getAnnotation(ResponseStatus.class).value()
                : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(body);
    }
}
