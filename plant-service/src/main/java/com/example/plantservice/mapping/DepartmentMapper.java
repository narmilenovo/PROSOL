package com.example.plantservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	Department mapToDepartment(DepartmentRequest departmentRequest);

	DepartmentResponse mapToDepartmentResponse(Department department);
}
