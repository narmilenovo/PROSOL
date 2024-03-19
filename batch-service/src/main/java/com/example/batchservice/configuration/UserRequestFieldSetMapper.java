package com.example.batchservice.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.example.batchservice.request.UserRequest;

@Component
public class UserRequestFieldSetMapper implements FieldSetMapper<UserRequest> {

	@Override
	public UserRequest mapFieldSet(FieldSet fieldSet) throws BindException {
		UserRequest userRequest = new UserRequest();
		userRequest.setEmail(fieldSet.readString("email"));
		userRequest.setPassword(fieldSet.readString("password"));
		userRequest.setConfirmPassword(fieldSet.readString("confirmPassword"));
		userRequest.setFirstName(fieldSet.readString("firstName"));
		userRequest.setLastName(fieldSet.readString("lastName"));
		userRequest.setPhone(fieldSet.readString("phone"));
		userRequest.setBusiness(fieldSet.readString("business"));
		userRequest.setDepartmentId(fieldSet.readLong("departmentId"));
		// Convert plantId from String array to List<Long>
		// Convert plantId from String array to List<Long>
		String plantIdsStr = fieldSet.readString("plantId");
		List<Long> plantIds = Arrays.stream(plantIdsStr.replaceAll("\\[|\\]", "").split(",")).map(Long::parseLong)
				.collect(Collectors.toList());
		userRequest.setPlantId(plantIds);
		userRequest.setStatus(fieldSet.readBoolean("status"));
		// Convert roles from String array to Long array
		String rolesStr = fieldSet.readString("roles");
		Long[] roles = Arrays.stream(rolesStr.replaceAll("\\[|\\]", "").split(",")).map(Long::parseLong)
				.toArray(Long[]::new);
		userRequest.setRoles(roles);

		return userRequest;
	}
}
