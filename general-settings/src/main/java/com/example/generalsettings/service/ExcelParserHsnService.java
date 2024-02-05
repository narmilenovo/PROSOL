package com.example.generalsettings.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.exception.AlreadyExistsException;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelParserHsnService {
	void saveDataToDatabase(MultipartFile file) throws AlreadyExistsException, IOException;

	List<String> getHeadersFromEntity();

	void exportEmptyExcel(HttpServletResponse response, List<String> headers);

	public List<Hsn> findAll();

	List<Map<String, Object>> convertHsnListToMap(List<Hsn> hsnS);

	void downloadTemplate(HttpServletResponse response) throws IOException;
}
