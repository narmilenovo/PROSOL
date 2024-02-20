package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface StorageBinService {

	StorageBinResponse saveStorageBin(@Valid StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	StorageBinResponse getStorageBinById(Long id) throws ResourceNotFoundException;

	List<StorageBinResponse> getAllStorageBin();

	List<StorageBin> findAll();

	StorageBinResponse updateStorageBin(Long id, StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	StorageBinResponse updateStatusUsingStorageBinId(Long id) throws ResourceNotFoundException;

	List<StorageBinResponse> updateBulkStatusStorageBinId(List<Long> id) throws ResourceNotFoundException;

	void deleteStorageBin(Long id) throws ResourceNotFoundException;

	void deleteBatchStorageBin(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertBinListToMap(List<StorageBin> bin);

}
