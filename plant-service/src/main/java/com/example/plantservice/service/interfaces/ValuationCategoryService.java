package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.entity.ValuationCategory;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface ValuationCategoryService {

	ValuationCategoryResponse saveValuationCategory(@Valid ValuationCategoryRequest valuationCategoryRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ValuationCategoryResponse getValuationCategoryById(Long id) throws ResourceNotFoundException;

	List<ValuationCategoryResponse> getAllValuationCategory();

	List<ValuationCategory> findAll();

	ValuationCategoryResponse updateValuationCategory(Long id, ValuationCategoryRequest valuationCategoryRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ValuationCategoryResponse updateStatusUsingValuationCategoryId(Long id) throws ResourceNotFoundException;

	List<ValuationCategoryResponse> updateBulkStatusValuationCategoryId(List<Long> id) throws ResourceNotFoundException;

	void deleteValuationCategory(Long id) throws ResourceNotFoundException;

	void deleteBatchValuationCategory(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertValuationCategoryListToMap(List<ValuationCategory> valuationCategory);
}
