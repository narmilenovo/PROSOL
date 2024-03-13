package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;

import com.example.mrpdataservice.entity.ScheduleMargin;
import com.example.mrpdataservice.request.ScheduleMarginRequest;
import com.example.mrpdataservice.response.ScheduleMarginResponse;

@Mapper(componentModel = "spring")
public interface ScheduleMarginMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	ScheduleMargin mapToScheduleMargin(ScheduleMarginRequest scheduleMarginRequest);

	ScheduleMarginResponse mapToScheduleMarginResponse(ScheduleMargin scheduleMargin);
}
