package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.MrpType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.MrpTypeRequest;
import com.example.mrpdataservice.response.MrpTypeResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface MrpTypeService {

	MrpTypeResponse saveMrpType(@Valid MrpTypeRequest mrpTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MrpTypeResponse getMrpTypeById(Long id) throws ResourceNotFoundException;

	List<MrpTypeResponse> getAllMrpType();

	List<MrpType> findAll();

	MrpTypeResponse updateMrpType(Long id, MrpTypeRequest mrpTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MrpTypeResponse updateStatusUsingMrpTypeId(Long id) throws ResourceNotFoundException;

	List<MrpTypeResponse> updateBulkStatusMrpTypeId(List<Long> id) throws ResourceNotFoundException;

	void deleteMrpType(Long id) throws ResourceNotFoundException;

	void deleteBatchMrpType(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertMrpTypeListToMap(List<MrpType> mrpControlReport);
}
