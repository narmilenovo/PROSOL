package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.VarianceKey;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface VarianceKeyService {

	VarianceKeyResponse saveVarianceKey(@Valid VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	VarianceKeyResponse getVarianceKeyById(Long id) throws ResourceNotFoundException;

	List<VarianceKeyResponse> getAllVarianceKey();

	List<VarianceKey> findAll();

	VarianceKeyResponse updateVarianceKey(Long id, VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	VarianceKeyResponse updateStatusUsingVarianceKeyId(Long id) throws ResourceNotFoundException;

	List<VarianceKeyResponse> updateBulkStatusVarianceKeyId(List<Long> id) throws ResourceNotFoundException;

	void deleteVarianceKey(Long id) throws ResourceNotFoundException;

	void deleteBatchVarianceKey(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertVarianceKeyListToMap(List<VarianceKey> varianceKey);

}
