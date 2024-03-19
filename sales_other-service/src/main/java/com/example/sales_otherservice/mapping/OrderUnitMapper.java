package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.entity.OrderUnit;

@Mapper(componentModel = "spring")
public interface OrderUnitMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	OrderUnit mapToOrderUnit(OrderUnitRequest orderUnitRequest);

	OrderUnitResponse mapToOrderUnitResponse(OrderUnit orderUnit);
}
