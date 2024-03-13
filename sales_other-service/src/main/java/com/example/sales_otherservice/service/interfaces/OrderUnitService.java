package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface OrderUnitService {
	OrderUnitResponse saveOu(OrderUnitRequest orderUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	OrderUnitResponse getOuById(@NonNull Long id) throws ResourceNotFoundException;

	List<OrderUnitResponse> getAllOu();

	List<OrderUnitResponse> findAllStatusTrue();

	OrderUnitResponse updateOuStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<OrderUnitResponse> updateBatchOuStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	OrderUnitResponse updateOu(@NonNull Long id, OrderUnitRequest updateOrderUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteOuById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchOu(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
