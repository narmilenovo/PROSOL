package com.example.batchservice.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class BadCredentialsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadCredentialsException(String message) {
        super(message);
    }
}
