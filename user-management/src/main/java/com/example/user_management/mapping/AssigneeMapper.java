package com.example.user_management.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.user_management.dto.request.AssigneeRequest;
import com.example.user_management.dto.response.AssigneeResponse;
import com.example.user_management.dto.response.UpdateAssigneeResponse;
import com.example.user_management.entity.Assignee;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring", uses = { RoleMapper.class, UserMapper.class })
public interface AssigneeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "role.id", source = "role")
	@Mapping(target = "subUser.id", source = "subUser")
	Assignee mapToAssignee(AssigneeRequest assigneeRequest);

	@Mapping(target = "subUser", source = "subUser.email")
	AssigneeResponse mapToAssigneeResponse(Assignee assignee);

	@Mapping(target = "subUser.email", source = "subUser")
	@Mapping(target = "users", ignore = true)
	Assignee mapAssigneeResponseToAssignee(AssigneeResponse assigneeResponse);

	@Mapping(target = "role", source = "role.name")
	@Mapping(target = "subUser", source = "subUser.email")
	UpdateAssigneeResponse assigneeToUpdateAssigneeResponse(Assignee assignee);

}
