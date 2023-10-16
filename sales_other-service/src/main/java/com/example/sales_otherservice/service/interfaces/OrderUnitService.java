package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface OrderUnitService {
    OrderUnitResponse saveOu(OrderUnitRequest orderUnitRequest) throws ResourceFoundException;

    List<OrderUnitResponse> getAllOu();

    OrderUnitResponse getOuById(Long id) throws ResourceNotFoundException;

    List<OrderUnitResponse> findAllStatusTrue();

    OrderUnitResponse updateOu(Long id, OrderUnitRequest updateOrderUnitRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteOuById(Long id) throws ResourceNotFoundException;
}
