package com.example.generalservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.entity.UnitOfIssue;

@Mapper(componentModel = "spring")
public interface UnitOfIssueMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	UnitOfIssue mapToUnitOfIssue(UnitOfIssueRequest unitOfIssueRequest);

	UnitOfIssueResponse mapToUnitOfIssueResponse(UnitOfIssue unitOfIssue);

}
