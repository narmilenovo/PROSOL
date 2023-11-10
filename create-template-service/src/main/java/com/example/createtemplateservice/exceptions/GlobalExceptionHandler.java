package com.example.createtemplateservice.exceptions;

import com.example.createtemplateservice.jpa.dto.response.BadRequestResponse;
import com.example.createtemplateservice.jpa.dto.response.GenericResponse;
import com.example.createtemplateservice.jpa.dto.response.InvalidDataResponse;
import com.example.createtemplateservice.utils.Helpers;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, String> formatMessage(String message) {
        Map<String, String> result = new HashMap<>();
        result.put("message", message);

        return result;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException ex) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceFoundException.class)
    public ResponseEntity<Object> resourceFoundException(ResourceFoundException ex) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<Object> passwordNotMatchException(PasswordNotMatchException ex) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> fileNotFoundException(FileNotFoundException ex) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));
        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /*@ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Object> fileStorageException(FileStorageException ex, WebRequest request) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));
        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv -> {
            String[] strings = cv.getPropertyPath().toString().split("\\.");

            errors.put(strings[strings.length - 1], cv.getMessage());
        });

        Map<String, Map<String, String>> result = new HashMap<>();
        result.put("errors", errors);

        GenericResponse response = new GenericResponse(result);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HashMap<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String field = "";

            if (objectError.getArguments() != null && objectError.getArguments().length >= 2) {
                field = objectError.getArguments()[1].toString();
            }

            if (!field.isEmpty()) {
                Helpers.updateErrorHashMap(errors, field, objectError.getDefaultMessage());
            }
        });

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> Helpers.updateErrorHashMap(errors, fieldError.getField(), fieldError.getDefaultMessage()));

        Map<String, Map<String, List<String>>> result = new HashMap<>();
        result.put("errors", errors);

        InvalidDataResponse response = new InvalidDataResponse(result);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException ex) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

   /* @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException ex, WebRequest request) {
        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }*/

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> globalExceptionHandler(Exception ex) {
        ex.printStackTrace();

        BadRequestResponse response = new BadRequestResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}