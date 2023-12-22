package com.example.vendor_masterservice.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceFoundException(String message) {
        super(message);
    }
}