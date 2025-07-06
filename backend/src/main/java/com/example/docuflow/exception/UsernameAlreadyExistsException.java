// src/main/java/com/example/docuflow/exception/UsernameAlreadyExistsException.java
package com.example.docuflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when someone tries to register with a username that’s already taken.
 * Automatically causes Spring to return a 400 Bad Request with a JSON body.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
