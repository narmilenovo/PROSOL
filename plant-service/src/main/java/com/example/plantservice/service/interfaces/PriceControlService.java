package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.PriceControl;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface PriceControlService {

	PriceControlResponse savePriceControl(@Valid PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	PriceControlResponse getPriceControlById(Long id) throws ResourceNotFoundException;

	List<PriceControlResponse> getAllPriceControl();

	List<PriceControl> findAll();

	PriceControlResponse updatePriceControl(Long id, PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	PriceControlResponse updateStatusUsingPriceControlId(Long id) throws ResourceNotFoundException;

	List<PriceControlResponse> updateBulkStatusPriceControlId(List<Long> id) throws ResourceNotFoundException;

	void deletePriceControl(Long id) throws ResourceNotFoundException;

	void deleteBatchPriceControl(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertPriceListToMap(List<PriceControl> price);

}
