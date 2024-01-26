package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface StorageLocationService {

	StorageLocationResponse saveStorageLocation(@Valid StorageLocationRequest storageLocationRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	StorageLocationResponse getStorageLocationById(Long id) throws ResourceNotFoundException;

	List<StorageLocationResponse> getAllStorageLocation();

	List<StorageLocation> findAll();

	List<StorageLocationResponse> getAllPlantByName(String name);

	StorageLocationResponse updateStorageLocation(Long id, StorageLocationRequest storageLocationRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	StorageLocationResponse updateStatusUsingStorageLocationId(Long id) throws ResourceNotFoundException;

	List<StorageLocationResponse> updateBulkStatusStorageLocationId(List<Long> id) throws ResourceNotFoundException;

	void deleteStorageLocation(Long id) throws ResourceNotFoundException;

	void deleteBatchStorageLocation(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertStorageLocationListToMap(List<StorageLocation> storage);

}
