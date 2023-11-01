package com.example.generalsettings.controller;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;
import com.example.generalsettings.service.EquipmentUnitService;
import com.example.generalsettings.util.GeneratePdfReport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EquipmentUnitController {
    private final EquipmentUnitService equipmentUnitService;
    
    @PostMapping("/saveEquipmentUnit")
    public ResponseEntity<Object> saveEquipmentUnit(@Valid @RequestBody EquipmentUnitRequest equipmentUnitRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveEquipmentUnit").toUriString());
        EquipmentUnitResponse savedEquipmentUnit = equipmentUnitService.saveEquipmentUnit(equipmentUnitRequest);
        return ResponseEntity.created(uri).body(savedEquipmentUnit);
    }

    @PutMapping("/updateEquipmentUnit/{id}")
    public ResponseEntity<Object> updateEquipmentUnit(@PathVariable Long id, @RequestBody EquipmentUnitRequest equipmentUnitRequest) throws ResourceNotFoundException, AlreadyExistsException {
        EquipmentUnitResponse updateEquipmentUnit = equipmentUnitService.updateEquipmentUnit(id, equipmentUnitRequest);
        return ResponseEntity.ok().body(updateEquipmentUnit);
    }

    @GetMapping("/getEquipmentUnitById/{id}")
    public ResponseEntity<Object> getEquipmentUnitById(@PathVariable Long id) throws ResourceNotFoundException {
        EquipmentUnitResponse foundEquipmentUnit = equipmentUnitService.getEquipmentUnitById(id);
        return ResponseEntity.ok(foundEquipmentUnit);
    }

    @DeleteMapping("/deleteEquipmentUnit/{id}")
    public ResponseEntity<String> deleteEquipmentUnit(@PathVariable Long id) throws ResourceNotFoundException {
        equipmentUnitService.deleteEquipmentUnit(id);
        return ResponseEntity.ok().body("EquipmentUnit of '" + id + "' is deleted");
    }

    @PatchMapping("/updateEquipmentUnitById/{id}")
    public ResponseEntity<Object> updateEquipmentUnitStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        EquipmentUnitResponse response = equipmentUnitService.updateStatusUsingEquipmentUnitId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusEquipmentUnitId/{id}")
    public ResponseEntity<Object> updateBulkStatusEquipmentUnitId(@PathVariable List<Long> id) {
        List<EquipmentUnitResponse> responseList = equipmentUnitService.updateBulkStatusEquipmentUnitId(id);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllEquipmentUnit")
    public ResponseEntity<Object> getAllEquipmentUnit() {
        List<EquipmentUnitResponse> equipmentUnit = equipmentUnitService.getAllEquipmentUnit();
        return ResponseEntity.ok(equipmentUnit);
    }

}
