package com.example.batchservice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.batchservice.client.DynamicServiceClient;
import com.example.batchservice.request.DepartmentRequest;
import com.example.batchservice.request.PlantRequest;
import com.example.batchservice.request.PriceControlRequest;
import com.example.batchservice.request.ProfitCenterRequest;
import com.example.batchservice.request.StorageBinRequest;
import com.example.batchservice.utils.ExcelFileHelper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantService {

	private final ExcelFileHelper excelFileHelper;
	private final DynamicServiceClient dynamicServiceClient;

	public void downloadPlantTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "Plant";
		Class<?> clazz = PlantRequest.class;
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Plant_";
		List<String> dynamicFields = dynamicServiceClient.getDynamicFieldsListInForm("Plant");
		excelFileHelper.exportTemplate(response, sheetName, clazz, contentType, extension, prefix, dynamicFields);
	}

	public void downloadDepartmentTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "Department";
		Class<?> clazz = DepartmentRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Department_";
		List<String> dynamicFields = dynamicServiceClient.getDynamicFieldsListInForm("Department");
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix, dynamicFields);
	}

	public void downloadPriceControlTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "PriceControl";
		Class<?> clazz = PriceControlRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "PriceControl_";
		List<String> dynamicFields = dynamicServiceClient.getDynamicFieldsListInForm("PriceControl");
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix, dynamicFields);
	}

	public void downloadProfitCenterTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ProfitCenter";
		Class<?> clazz = ProfitCenterRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ProfitCenter_";
		List<String> dynamicFields = dynamicServiceClient.getDynamicFieldsListInForm("ProfitCenter");
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix, dynamicFields);
	}

	public void downloadStorageBinTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "StorageBin";
		Class<?> clazz = StorageBinRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "StorageBin_";
		List<String> dynamicFields = dynamicServiceClient.getDynamicFieldsListInForm("ProfitCenter");
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix, dynamicFields);

	}
}
