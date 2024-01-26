package com.example.sales_otherservice.exceptions;

public class DateTimeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateTimeException(String message) {
		super(message);
	}

	public DateTimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
