package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.entity.OrderUnit;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.OrderUnitRepository;
import com.example.sales_otherservice.service.interfaces.OrderUnitService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderUnitServiceImpl implements OrderUnitService {
    private final OrderUnitRepository orderUnitRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderUnitResponse saveOu(OrderUnitRequest orderUnitRequest) throws ResourceFoundException {
        String ouCode = orderUnitRequest.getOuCode();
        String ouName = orderUnitRequest.getOuName();
        boolean exists = orderUnitRepository.existsByOuCodeOrOuName(ouCode, ouName);
        if (!exists) {

            OrderUnit orderUnit = modelMapper.map(orderUnitRequest, OrderUnit.class);
            OrderUnit savedUnit = orderUnitRepository.save(orderUnit);
            return mapToOrderUnitResponse(savedUnit);
        }
        throw new ResourceFoundException("Order Unit Already exists");
    }

    @Override
    public List<OrderUnitResponse> getAllOu() {
        List<OrderUnit> orderUnits = orderUnitRepository.findAll();
        return orderUnits.stream().map(this::mapToOrderUnitResponse).toList();

    }

    @Override
    public OrderUnitResponse getOuById(Long id) throws ResourceNotFoundException {
        OrderUnit orderUnit = this.findOuById(id);
        return mapToOrderUnitResponse(orderUnit);
    }

    @Override
    public List<OrderUnitResponse> findAllStatusTrue() {
        List<OrderUnit> orderUnits = orderUnitRepository.findAllByOuStatusIsTrue();
        return orderUnits.stream().map(this::mapToOrderUnitResponse).toList();
    }

    @Override
    public OrderUnitResponse updateOu(Long id, OrderUnitRequest updateOrderUnitRequest) throws ResourceNotFoundException, ResourceFoundException {
        String ouCode = updateOrderUnitRequest.getOuCode();
        String ouName = updateOrderUnitRequest.getOuName();
        OrderUnit existingOrderUnit = this.findOuById(id);
        boolean exists = orderUnitRepository.existsByOuCodeAndIdNotOrOuNameAndIdNot(ouCode, id, ouName, id);
        if (!exists) {
            modelMapper.map(updateOrderUnitRequest, existingOrderUnit);
            OrderUnit updatedOrderUnit = orderUnitRepository.save(existingOrderUnit);
            return mapToOrderUnitResponse(updatedOrderUnit);
        }
        throw new ResourceFoundException("Order Unit Already exists");
    }

    @Override
    public void deleteOuById(Long id) throws ResourceNotFoundException {
        OrderUnit orderUnit = this.findOuById(id);
        orderUnitRepository.deleteById(orderUnit.getId());
    }

    private OrderUnitResponse mapToOrderUnitResponse(OrderUnit orderUnit) {
        return modelMapper.map(orderUnit, OrderUnitResponse.class);
    }

    private OrderUnit findOuById(Long id) throws ResourceNotFoundException {
        Optional<OrderUnit> orderUnit = orderUnitRepository.findById(id);
        if (orderUnit.isEmpty()) {
            throw new ResourceNotFoundException("Order Unit not found with this Id");
        }
        return orderUnit.get();
    }
}
