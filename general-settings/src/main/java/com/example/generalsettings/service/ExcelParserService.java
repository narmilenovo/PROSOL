package com.example.generalsettings.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.exception.AlreadyExistsException;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelParserService {
	void saveDataToDatabase(MultipartFile file) throws AlreadyExistsException, IOException;

	List<String> getHeadersFromEntity();

	void exportEmptyExcel(HttpServletResponse response, List<String> headers);

	List<AttributeUom> findAll();

	List<Map<String, Object>> convertUomListToMap(List<AttributeUom> uom);

	void downloadTemplate(HttpServletResponse response) throws IOException;
}
