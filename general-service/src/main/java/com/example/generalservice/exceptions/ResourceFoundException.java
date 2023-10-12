package com.example.generalservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.FOUND)
public class ResourceFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceFoundException(String message) {
        super(message);
    }
}