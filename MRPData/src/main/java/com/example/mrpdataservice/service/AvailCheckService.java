package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.AvailCheck;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.AvailCheckRequest;
import com.example.mrpdataservice.response.AvailCheckResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AvailCheckService {

	AvailCheckResponse saveAvailCheck(@Valid AvailCheckRequest availCheckRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	AvailCheckResponse getAvailCheckById(Long id) throws ResourceNotFoundException;

	List<AvailCheckResponse> getAllAvailCheck();

	List<AvailCheck> findAll();

	AvailCheckResponse updateAvailCheck(Long id, AvailCheckRequest availCheckRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	AvailCheckResponse updateStatusUsingAvailCheckId(Long id) throws ResourceNotFoundException;

	List<AvailCheckResponse> updateBulkStatusAvailCheckId(List<Long> id) throws ResourceNotFoundException;

	void deleteAvailCheck(Long id) throws ResourceNotFoundException;

	void deleteBatchAvailCheck(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertDepartmentListToMap(List<AvailCheck> availCheckReport);
}
