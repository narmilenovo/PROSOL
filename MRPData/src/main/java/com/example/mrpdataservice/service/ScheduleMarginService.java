package com.example.mrpdataservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.entity.ScheduleMargin;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.ScheduleMarginRequest;
import com.example.mrpdataservice.response.ScheduleMarginResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface ScheduleMarginService {

	ScheduleMarginResponse saveScheduleMargin(@Valid ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ScheduleMarginResponse getScheduleMarginById(Long id) throws ResourceNotFoundException;

	ScheduleMarginResponse updateScheduleMargin(Long id, ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ScheduleMarginResponse updateStatusUsingScheduleMarginId(Long id) throws ResourceNotFoundException;

	List<ScheduleMarginResponse> updateBulkStatusScheduleMarginId(List<Long> id) throws ResourceNotFoundException;

	List<ScheduleMarginResponse> getAllScheduleMargin();

	List<ScheduleMargin> findAll();

	void deleteScheduleMargin(Long id) throws ResourceNotFoundException;

	void deleteBatchScheduleMargin(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

	List<Map<String, Object>> convertScheduleMarginListToMap(List<ScheduleMargin> scheduleMarginReport);

}
