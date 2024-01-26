package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.Department;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface DepartmentService {

	DepartmentResponse saveDepartment(@Valid DepartmentRequest departmentRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	List<DepartmentResponse> getAllDepartments() throws ResourceNotFoundException;

	void deleteDepartment(Long id) throws ResourceNotFoundException;

	DepartmentResponse getDepartmentById(Long id) throws ResourceNotFoundException;

	DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	DepartmentResponse updateStatusUsingDepartmentId(Long id) throws ResourceNotFoundException;

	List<DepartmentResponse> updateBulkStatusDepartmentId(List<Long> id) throws ResourceNotFoundException;

	List<Department> findAll() throws ResourceNotFoundException;

	List<Map<String, Object>> convertDepartmentListToMap(List<Department> department);

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void downloadAllData(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void deleteBatchDepartment(List<Long> ids) throws ResourceNotFoundException;
}
