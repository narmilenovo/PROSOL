package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.Department;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.DepartmentRepo;
import com.example.plantservice.service.interfaces.DepartmentService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
	private static final String DEPARTMENT_NOT_FOUND_MESSAGE = null;

	private final DepartmentRepo departmentRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper = new ModelMapper();
	private final DynamicClient dynamicClient;

	@Override
	public DepartmentResponse saveDepartment(DepartmentRequest departmentRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = departmentRepo.existsByDepartmentName(departmentRequest.getDepartmentName());
		if (!exists) {
			Department department = modelMapper.map(departmentRequest, Department.class);
			for (Map.Entry<String, Object> entryField : department.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Department.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			departmentRepo.save(department);
			return mapToDepartmentResponse(department);
		} else {
			throw new AlreadyExistsException("Department with this name already exists");
		}
	}

	@Override
	public DepartmentResponse getDepartmentById(Long id) throws ResourceNotFoundException {
		Department department = this.findDepartmentById(id);
		return mapToDepartmentResponse(department);
	}

	@Override
	public List<DepartmentResponse> getAllDepartments() throws ResourceNotFoundException {
		List<Department> departments = this.findAll();
		return departments.stream()
				.map(this::mapToDepartmentResponse)
				.collect(Collectors.toList());
	}

	@Override
	public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String departmentName = departmentRequest.getDepartmentName();
		boolean exists = departmentRepo.existsByDepartmentNameAndIdNot(departmentName, id);
		if (!exists) {
			Department existingDepartment = this.findDepartmentById(id);
			modelMapper.map(departmentRequest, existingDepartment);
			for (Map.Entry<String, Object> entryField : existingDepartment.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Department.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			departmentRepo.save(existingDepartment);
			return mapToDepartmentResponse(existingDepartment);
		} else {
			throw new AlreadyExistsException("Department with this name already exists");
		}
	}

	@Override
	public DepartmentResponse updateStatusUsingDepartmentId(Long id) throws ResourceNotFoundException {
		Department existingPlant = this.findDepartmentById(id);
		existingPlant.setDepartmentStatus(!existingPlant.getDepartmentStatus());
		departmentRepo.save(existingPlant);
		return mapToDepartmentResponse(existingPlant);
	}

	@Override
	public List<DepartmentResponse> updateBulkStatusDepartmentId(List<Long> ids) throws ResourceNotFoundException {
		List<Department> existingDepartment = this.findAllById(ids);
		for (Department department : existingDepartment) {
			department.setDepartmentStatus(!department.getDepartmentStatus());
		}
		departmentRepo.saveAll(existingDepartment);
		return existingDepartment.stream().map(this::mapToDepartmentResponse).toList();
	}

	public void deleteDepartment(Long id) throws ResourceNotFoundException {
		Department department = this.findDepartmentById(id);
		departmentRepo.deleteById(department.getId());
	}

	@Override
	public void deleteBatchDepartment(List<Long> ids) throws ResourceNotFoundException {
		List<Department> departments = this.findAllById(ids);
		departmentRepo.deleteAll(departments);
	}

	private DepartmentResponse mapToDepartmentResponse(Department department) {
		return modelMapper.map(department, DepartmentResponse.class);
	}

	private Department findDepartmentById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<Department> department = departmentRepo.findById(id);
		if (department.isEmpty()) {
			throw new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE);
		}
		return department.get();
	}

	public List<Department> findAll() throws ResourceNotFoundException {
		List<Department> departments = departmentRepo.findAllByOrderByIdAsc();
		if (departments != null) {
			return departments;
		} else {
			throw new ResourceNotFoundException("Departments is Empty");
		}
	}

	private List<Department> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<Department> departments = departmentRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> departments.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Department with IDs " + missingIds + " not found.");
		}
		return departments;
	}

	@Override
	public List<Map<String, Object>> convertDepartmentListToMap(List<Department> deptList) {
		List<Map<String, Object>> department = new ArrayList<>();

		for (Department dept : deptList) {
			Map<String, Object> deptData = new HashMap<>();
			deptData.put("Id", dept.getId());
			deptData.put("Name", dept.getDepartmentName());
			deptData.put("Status", dept.getDepartmentStatus());
			department.add(deptData);
		}
		return department;
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "Department";
		Class<?> clazz = DepartmentRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Department_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<Department> department = excelFileHelper.readDataFromExcel(file.getInputStream(), Department.class);
		for (Department dept : department) {
			if (!departmentRepo.existsByDepartmentName(dept.getDepartmentName())) {
				this.departmentRepo.save(dept);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException {
		String sheetName = "Department";
		Class<?> clazz = DepartmentResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Department_";
		List<DepartmentResponse> allValue = getAllDepartments();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}
}