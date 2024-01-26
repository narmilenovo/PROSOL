package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.ValuationMaterialResponse;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.entity.ValuationClass;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface ValuationClassService {

	ValuationClassResponse saveValuationClass(@Valid ValuationClassRequest valuationClassRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ValuationClassResponse getValuationClassById(Long id) throws ResourceNotFoundException;

	ValuationMaterialResponse getValuationMaterialById(Long id) throws ResourceNotFoundException;

	List<ValuationClassResponse> getAllValuationClass() throws ResourceNotFoundException;

	List<ValuationMaterialResponse> getAllValuationClassByMaterial() throws ResourceNotFoundException;

	ValuationClassResponse updateValuationClass(Long id, ValuationClassRequest valuationClassRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ValuationClassResponse updateStatusUsingValuationClassId(Long id) throws ResourceNotFoundException;

	List<ValuationClassResponse> updateBulkStatusValuationClassId(List<Long> id) throws ResourceNotFoundException;

	void deleteValuationClass(Long id) throws ResourceNotFoundException;

	void deleteBatchValuationClass(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException;

	List<Map<String, Object>> convertValuationClassListToMap(List<ValuationClass> valuationClass);

	List<ValuationClass> findAllValuationClass() throws ResourceNotFoundException;
}
