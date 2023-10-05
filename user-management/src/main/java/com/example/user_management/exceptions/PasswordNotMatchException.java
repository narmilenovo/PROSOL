package com.example.user_management.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PasswordNotMatchException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public PasswordNotMatchException(String message) {
        super(message);
    }
}