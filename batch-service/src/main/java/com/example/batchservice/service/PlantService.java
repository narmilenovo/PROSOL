package com.example.batchservice.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.batchservice.request.PlantRequest;
import com.example.batchservice.utils.ExcelFileHelper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantService {

	private final ExcelFileHelper excelFileHelper;

	public void downloadPlantTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "Plant";
		Class<?> clazz = PlantRequest.class;
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Plant_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contentType, extension, prefix);
	}

}
