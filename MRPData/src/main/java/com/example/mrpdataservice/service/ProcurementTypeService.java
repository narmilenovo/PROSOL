package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.ProcurementType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.ProcurementTypeRequest;
import com.example.mrpdataservice.response.ProcurementTypeResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface ProcurementTypeService {

	ProcurementTypeResponse saveProcurementType(@Valid ProcurementTypeRequest procurementTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ProcurementTypeResponse getProcurementTypeById(Long id) throws ResourceNotFoundException;

	List<ProcurementTypeResponse> getAllProcurementType();

	List<ProcurementType> findAll();

	ProcurementTypeResponse updateProcurementType(Long id, ProcurementTypeRequest procurementTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ProcurementTypeResponse updateStatusUsingProcurementTypeId(Long id) throws ResourceNotFoundException;

	List<ProcurementTypeResponse> updateBulkStatusProcurementTypeId(List<Long> id) throws ResourceNotFoundException;

	void deleteProcurementType(Long id) throws ResourceNotFoundException;

	void deleteBatchProcurementType(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertProcurementTypeListToMap(List<ProcurementType> procurementTypeReport);

}
