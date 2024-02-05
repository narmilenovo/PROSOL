package com.example.generalsettings.exception;

public class ExcelFileException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExcelFileException(String message) {
		super(message);
	}

	public ExcelFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
