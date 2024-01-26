package com.example.plantservice.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.plantservice.config.GeneratePdfReport;
import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.Department;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.DepartmentService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
	private final GeneratePdfReport generatePdfReport;
	private final DepartmentService departmentService;

	@PostMapping("/saveDepartment")
	public ResponseEntity<Object> saveDepartment(@Valid @RequestBody DepartmentRequest departmentRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDepartment").toUriString());
		DepartmentResponse savedDepartment = departmentService.saveDepartment(departmentRequest);
		return ResponseEntity.created(uri).body(savedDepartment);
	}

	@GetMapping("/getDepartmentById/{id}")
	public ResponseEntity<Object> getDepartmentById(@PathVariable Long id) throws ResourceNotFoundException {
		DepartmentResponse foundDepartment = departmentService.getDepartmentById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundDepartment);
	}

	@GetMapping("/getAllDepartment")
	public ResponseEntity<Object> getAllDepartments() throws ResourceNotFoundException {
		List<DepartmentResponse> departments = departmentService.getAllDepartments();
		return ResponseEntity.status(HttpStatus.OK).body(departments);
	}

	@PutMapping("/updateDepartment/{id}")
	public ResponseEntity<Object> updateDepartment(@PathVariable Long id,
			@RequestBody DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException {
		DepartmentResponse updateDepartment = departmentService.updateDepartment(id, departmentRequest);
		return ResponseEntity.status(HttpStatus.OK).body(updateDepartment);
	}

	@PatchMapping("/updateDepartmentStatusById/{id}")
	public ResponseEntity<Object> updateDepartmentStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		DepartmentResponse response = departmentService.updateStatusUsingDepartmentId(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("/updateBulkStatusDepartmentId")
	public ResponseEntity<Object> updateBulkStatusDepartmentId(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		List<DepartmentResponse> responseList = departmentService.updateBulkStatusDepartmentId(ids);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteDepartment/{id}")
	public ResponseEntity<Object> deleteDepartment(@PathVariable Long id) throws ResourceNotFoundException {
		departmentService.deleteDepartment(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/deleteBatchDepartment")
	public ResponseEntity<Object> deleteBatchDepartment(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		departmentService.deleteBatchDepartment(ids);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/exportTemplateDept")
	public void exportExcelTemplateDept(HttpServletResponse response) throws IOException {
		departmentService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataDepartment", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		departmentService.importExcelSave(file);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/exportDataDept")
	public ResponseEntity<Object> exportExcelDataDept(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException {
		departmentService.downloadAllData(response);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/pdfDepartmentReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() throws ResourceNotFoundException {
		List<Department> department = departmentService.findAll();
		List<Map<String, Object>> data = departmentService.convertDepartmentListToMap(department);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "DepartmentReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "DepartmentReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
