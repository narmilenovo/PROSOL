package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.PlanningStrategyGrp;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.PlanningStrgyGrpRequest;
import com.example.mrpdataservice.response.PlanningStrgyGrpResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface PlanningStrgyGrpService {

	PlanningStrgyGrpResponse savePlanningStrgyGrp(@Valid PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	PlanningStrgyGrpResponse getPlanningStrgyGrpById(Long id) throws ResourceNotFoundException;

	List<PlanningStrgyGrpResponse> getAllPlanningStrgyGrp();

	List<PlanningStrategyGrp> findAll();

	PlanningStrgyGrpResponse updatePlanningStrgyGrp(Long id, PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	PlanningStrgyGrpResponse updateStatusUsingPlanningStrgyGrpId(Long id) throws ResourceNotFoundException;

	List<PlanningStrgyGrpResponse> updateBulkStatusPlanningStrgyGrpId(List<Long> id) throws ResourceNotFoundException;

	void deletePlanningStrgyGrp(Long id) throws ResourceNotFoundException;

	void deleteBatchPlanningStrgyGrp(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertPlanningStrategyGrpListToMap(List<PlanningStrategyGrp> planningStrategyGrpReport);
}
