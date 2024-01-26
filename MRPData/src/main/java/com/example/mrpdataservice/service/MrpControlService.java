package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.client.MrpPlantResponse;
import com.example.mrpdataservice.entity.MrpControl;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.MrpControlRequest;
import com.example.mrpdataservice.response.MrpControlResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface MrpControlService {

	MrpControlResponse saveMrpControl(MrpControlRequest mrpControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MrpControlResponse getMrpControlById(Long id) throws ResourceNotFoundException;

	MrpControlResponse getMrpControlByName(String name) throws ResourceNotFoundException;

	List<MrpControlResponse> getAllMrpControl();

	List<MrpPlantResponse> getAllMrpControlByPlant() throws ResourceNotFoundException;

	List<MrpControl> findAll();

	MrpControlResponse updateMrpControl(Long id, MrpControlRequest mrpControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MrpControlResponse updateStatusUsingMrpControlId(Long id) throws ResourceNotFoundException;

	List<MrpControlResponse> updateBulkStatusMrpControlId(List<Long> id) throws ResourceNotFoundException;

	void deleteMrpControl(Long id) throws ResourceNotFoundException;

	void deleteBatchMrpControl(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertMrpControlListToMap(List<MrpControl> mrpControlReport);
}
