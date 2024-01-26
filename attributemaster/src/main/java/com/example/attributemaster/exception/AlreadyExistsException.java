package com.example.attributemaster.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AlreadyExistsException extends Exception{
	 @Serial
	    private static final long serialVersionUID = 1L;
	  public AlreadyExistsException(String message) {
	        super(message);
	    }
}
