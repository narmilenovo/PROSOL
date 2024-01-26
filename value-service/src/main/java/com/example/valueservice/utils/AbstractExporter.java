package com.example.valueservice.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse httpServletResponse, String contentType, String extension,
			String prefix) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String timeStamp = dateFormat.format(new Date());
		String fileName = prefix + timeStamp + extension;
		httpServletResponse.setContentType(contentType);
		String headerKey = "Content-Disposition";
		String headerValue = "attachment;filename=" + fileName;
		httpServletResponse.setHeader(headerKey, headerValue);
	}
}
