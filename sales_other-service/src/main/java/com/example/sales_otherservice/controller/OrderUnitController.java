package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.OrderUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderUnitController {
    private final OrderUnitService orderUnitService;

    @PostMapping("/saveOu")
    public ResponseEntity<Object> saveOu(@Valid @RequestBody OrderUnitRequest orderUnitRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveOu").toUriString());
        OrderUnitResponse saveOu = orderUnitService.saveOu(orderUnitRequest);
        return ResponseEntity.created(uri).body(saveOu);
    }

    @GetMapping("/getAllOu")
    public ResponseEntity<Object> getAllOu() {
        List<OrderUnitResponse> allOu = orderUnitService.getAllOu();
        return ResponseEntity.ok(allOu);
    }


    @GetMapping("/getOuById/{id}")
    public ResponseEntity<Object> getOuById(@PathVariable Long id) throws ResourceNotFoundException {
        OrderUnitResponse dpById = orderUnitService.getOuById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllOuTrue")
    public ResponseEntity<Object> listOuStatusTrue() {
        List<OrderUnitResponse> unitResponses = orderUnitService.findAllStatusTrue();
        return ResponseEntity.ok(unitResponses);
    }

    @PutMapping("/updateOu/{id}")
    public ResponseEntity<Object> updateOu(@PathVariable Long id, @Valid @RequestBody OrderUnitRequest updateOrderUnitRequest) throws ResourceNotFoundException, ResourceFoundException {
        OrderUnitResponse updateOu = orderUnitService.updateOu(id, updateOrderUnitRequest);
        return ResponseEntity.ok(updateOu);
    }

    @DeleteMapping("/deleteOu/{id}")
    public ResponseEntity<Object> deleteOu(@PathVariable Long id) throws ResourceNotFoundException {
        orderUnitService.deleteOuById(id);
        return ResponseEntity.noContent().build();
    }
}
