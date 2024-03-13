package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.Department;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.DepartmentMapper;
import com.example.plantservice.repository.DepartmentRepo;
import com.example.plantservice.service.interfaces.DepartmentService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

	private final DepartmentRepo departmentRepo;
	private final ExcelFileHelper excelFileHelper;
	private final DepartmentMapper departmentMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DepartmentResponse saveDepartment(DepartmentRequest departmentRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		String departmentName = Helpers.capitalize(departmentRequest.getDepartmentName());
		if (departmentRepo.existsByDepartmentName(departmentName)) {
			throw new AlreadyExistsException("Department with this name already exists");
		}
		Department department = departmentMapper.mapToDepartment(departmentRequest);
		validateDynamicFields(department);
		departmentRepo.save(department);
		return departmentMapper.mapToDepartmentResponse(department);
	}

	@Override
	public DepartmentResponse getDepartmentById(Long id) throws ResourceNotFoundException {
		Department department = this.findDepartmentById(id);
		return departmentMapper.mapToDepartmentResponse(department);
	}

	@Override
	public List<DepartmentResponse> getAllDepartments() throws ResourceNotFoundException {
		return this.findAll().stream().map(departmentMapper::mapToDepartmentResponse).toList();
	}

	@Override
	public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String departmentName = Helpers.capitalize(departmentRequest.getDepartmentName());
		boolean exists = departmentRepo.existsByDepartmentNameAndIdNot(departmentName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			Department existingDepartment = this.findDepartmentById(id);
			if (!existingDepartment.getDepartmentName().equals(departmentName)) {
				auditFields.add(new AuditFields(null, "Department Name", existingDepartment.getDepartmentName(),
						departmentName));
				existingDepartment.setDepartmentName(departmentName);
			}

			if (!existingDepartment.getDepartmentStatus().equals(departmentRequest.getDepartmentStatus())) {
				auditFields.add(new AuditFields(null, "Department Status", existingDepartment.getDepartmentStatus(),
						departmentRequest.getDepartmentStatus()));
				existingDepartment.setDepartmentStatus(departmentRequest.getDepartmentStatus());
			}
			if (!existingDepartment.getDynamicFields().equals(departmentRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : departmentRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingDepartment.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingDepartment.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingDepartment.updateAuditHistory(auditFields);
			departmentRepo.save(existingDepartment);
			return departmentMapper.mapToDepartmentResponse(existingDepartment);
		} else {
			throw new AlreadyExistsException("Department with this name already exists");
		}
	}

	@Override
	public DepartmentResponse updateStatusUsingDepartmentId(Long id) throws ResourceNotFoundException {
		Department existingDepartment = this.findDepartmentById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingDepartment.getDepartmentStatus() != null) {
			auditFields.add(new AuditFields(null, "Department Status", existingDepartment.getDepartmentStatus(),
					!existingDepartment.getDepartmentStatus()));
			existingDepartment.setDepartmentStatus(!existingDepartment.getDepartmentStatus());
		}
		existingDepartment.updateAuditHistory(auditFields);
		departmentRepo.save(existingDepartment);
		return departmentMapper.mapToDepartmentResponse(existingDepartment);
	}

	@Override
	public List<DepartmentResponse> updateBulkStatusDepartmentId(List<Long> ids) throws ResourceNotFoundException {
		List<Department> existingDepartments = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingDepartments.forEach(existingDepartment -> {
			if (existingDepartment.getDepartmentStatus() != null) {
				auditFields.add(new AuditFields(null, "Department Status", existingDepartment.getDepartmentStatus(),
						!existingDepartment.getDepartmentStatus()));
				existingDepartment.setDepartmentStatus(!existingDepartment.getDepartmentStatus());
			}
			existingDepartment.updateAuditHistory(auditFields);
		});
		departmentRepo.saveAll(existingDepartments);
		return existingDepartments.stream().map(departmentMapper::mapToDepartmentResponse).toList();
	}

	public void deleteDepartment(Long id) throws ResourceNotFoundException {
		Department department = this.findDepartmentById(id);
		if (department != null) {
			departmentRepo.delete(department);
		}
	}

	@Override
	public void deleteBatchDepartment(List<Long> ids) throws ResourceNotFoundException {
		List<Department> departments = this.findAllById(ids);
		if (!departments.isEmpty()) {
			departmentRepo.deleteAll(departments);
		}
	}

	public List<Department> findAll() throws ResourceNotFoundException {
		List<Department> departments = departmentRepo.findAllByOrderByIdAsc();
		if (departments != null) {
			return departments;
		} else {
			throw new ResourceNotFoundException("Departments is Empty");
		}
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

	private void validateDynamicFields(Department department) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : department.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = Department.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private Department findDepartmentById(Long id) throws ResourceNotFoundException {
		return departmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department with ID " + id + " not found"));
	}

	private List<Department> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<Department> departments = departmentRepo.findAllById(ids);
		List<Department> foundDepartments = departments.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Department with IDs " + missingIds + " not found.");
		}
		return foundDepartments;
	}

}
