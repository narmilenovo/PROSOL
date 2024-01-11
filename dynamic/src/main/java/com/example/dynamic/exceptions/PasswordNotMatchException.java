package com.example.dynamic.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PasswordNotMatchException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public PasswordNotMatchException(String message) {
        super(message);
    }
}