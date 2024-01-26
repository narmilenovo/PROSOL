package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.ProfitCenter;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface ProfitCenterService {

	ProfitCenterResponse saveProfitCenter(@Valid ProfitCenterRequest profitCenterRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ProfitCenterResponse getProfitCenterById(Long id) throws ResourceNotFoundException;

	List<ProfitCenterResponse> getAllProfitCenter();

	List<ProfitCenter> findAll();

	ProfitCenterResponse updateProfitCenter(Long id, ProfitCenterRequest profitCenterRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ProfitCenterResponse updateStatusUsingProfitCenterId(Long id) throws ResourceNotFoundException;

	List<ProfitCenterResponse> updateBulkStatusProfitCenterId(List<Long> id) throws ResourceNotFoundException;

	void deleteProfitCenter(Long id) throws ResourceNotFoundException;

	void deleteBatchProfitCenter(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertProfitListToMap(List<ProfitCenter> profit);

}
