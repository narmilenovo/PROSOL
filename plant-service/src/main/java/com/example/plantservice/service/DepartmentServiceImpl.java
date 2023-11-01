package com.example.plantservice.service;

import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.Department;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.DepartmentRepo;
import com.example.plantservice.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private static final String DEPARTMENT_NOT_FOUND_MESSAGE = null;

    private final DepartmentRepo departmentRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> department = departmentRepo.findAll();
        return department.stream().map(this::mapToDepartmentResponse).toList();
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException {
        Optional<Department> existDepartmentName = departmentRepo.findByDepartmentName(departmentRequest.getDepartmentName());
        if (existDepartmentName.isPresent() && !existDepartmentName.get().getDepartmentName().equals(departmentRequest.getDepartmentName())) {
            throw new AlreadyExistsException("Department with this name already exists");
        } else {
            Department existingDepartment = this.findDepartmentById(id);
            modelMapper.map(departmentRequest, existingDepartment);
            departmentRepo.save(existingDepartment);
            return mapToDepartmentResponse(existingDepartment);
        }
    }

    public void deleteDepartment(Long id) throws ResourceNotFoundException {
        Department department = this.findDepartmentById(id);
        departmentRepo.deleteById(department.getId());
    }

    @Override
    public DepartmentResponse saveDepartment(DepartmentRequest departmentRequest) throws  AlreadyExistsException {
        Optional<Department> existDepartmentName = departmentRepo.findByDepartmentName(departmentRequest.getDepartmentName());
        if (existDepartmentName.isPresent()) {
            throw new AlreadyExistsException("Department with this name already exists");
        }
        else {
            Department department = modelMapper.map(departmentRequest, Department.class);
            departmentRepo.save(department);
            return mapToDepartmentResponse(department);
        }
    }


    private DepartmentResponse mapToDepartmentResponse(Department department) {
        return modelMapper.map(department, DepartmentResponse.class);
    }


    private Department findDepartmentById(Long id) throws ResourceNotFoundException {
        Optional<Department> department = departmentRepo.findById(id);
        if (department.isEmpty()) {
            throw new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE);
        }
        return department.get();
    }



    @Override
    public DepartmentResponse getDepartmentById(Long id) throws ResourceNotFoundException {
        Department department = this.findDepartmentById(id);
        return mapToDepartmentResponse(department);
    }

    @Override
    public List<DepartmentResponse> updateBulkStatusDepartmentId(List<Long> id) {
        List<Department> existingDepartment = departmentRepo.findAllById(id);
        for (Department department : existingDepartment) {
            department.setStatus(!department.getStatus());
        }
        departmentRepo.saveAll(existingDepartment);
        return existingDepartment.stream().map(this::mapToDepartmentResponse).toList();
    }

    @Override
    public DepartmentResponse updateStatusUsingDepartmentId(Long id) throws ResourceNotFoundException {
        Department existingPlant = this.findDepartmentById(id);
        existingPlant.setStatus(!existingPlant.getStatus());
        departmentRepo.save(existingPlant);
        return mapToDepartmentResponse(existingPlant);
    }
}
