package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.LotSize;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.LotSizeRequest;
import com.example.mrpdataservice.response.LotSizeResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface LotSizeService {

	LotSizeResponse saveLotSize(@Valid LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	LotSizeResponse getLotSizeById(Long id) throws ResourceNotFoundException;

	List<LotSizeResponse> getAllLotSize();

	List<LotSize> findAll();

	LotSizeResponse updateLotSize(Long id, LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	LotSizeResponse updateStatusUsingLotSizeId(Long id) throws ResourceNotFoundException;

	List<LotSizeResponse> updateBulkStatusLotSizeId(List<Long> id) throws ResourceNotFoundException;

	void deleteLotSize(Long id) throws ResourceNotFoundException;

	void deleteBatchLotSize(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertLotSizeListToMap(List<LotSize> lotSizeReport);
}
