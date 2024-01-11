package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface OrderUnitService {
	OrderUnitResponse saveOu(OrderUnitRequest orderUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<OrderUnitResponse> getAllOu();

	OrderUnitResponse getOuById(Long id) throws ResourceNotFoundException;

	List<OrderUnitResponse> findAllStatusTrue();

	OrderUnitResponse updateOu(Long id, OrderUnitRequest updateOrderUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteOuById(Long id) throws ResourceNotFoundException;

	void deleteBatchOu(List<Long> ids);
}
