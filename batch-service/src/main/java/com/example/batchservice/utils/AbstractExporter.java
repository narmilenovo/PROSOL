package com.example.batchservice.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse httpServletResponse, String contentType, String extension,
			String prefix) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String timeStamp = dateFormat.format(new Date());
		String fileName = prefix + timeStamp + extension;
		httpServletResponse.setContentType(contentType);
		String headerKey = HttpHeaders.CONTENT_DISPOSITION;
		String headerValue = "attachment; filename=" + fileName;
		httpServletResponse.setHeader(headerKey, headerValue);
	}
}
