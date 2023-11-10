package com.example.valueservice.controller;

import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ResourceFoundException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.service.interfaces.ValueMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ValueMasterController {

    private final ValueMasterService valueMasterService;


    @PostMapping("/saveValue")
    public ResponseEntity<Object> saveValue(@Valid @RequestBody ValueMasterRequest valueMasterRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValue").toUriString());
        ValueMasterResponse savedValue = valueMasterService.saveValue(valueMasterRequest);
        return ResponseEntity.created(uri).body(savedValue);
    }

    @GetMapping("/getAllValue")
    public ResponseEntity<Object> getAllValue() {
        List<ValueMasterResponse> allValue = valueMasterService.getAllValue();
        return ResponseEntity.ok(allValue);
    }


    @GetMapping("/getValueById/{id}")
    public ResponseEntity<Object> getValueById(@PathVariable Long id) throws ResourceNotFoundException {
        ValueMasterResponse valueById = valueMasterService.getValueById(id);
        return ResponseEntity.ok(valueById);
    }

    @PutMapping("/updateValue/{id}")
    public ResponseEntity<Object> updateValue(@PathVariable Long id, @Valid @RequestBody ValueMasterRequest updateValueMasterRequest) throws ResourceNotFoundException, ResourceFoundException {
        ValueMasterResponse updateValue = valueMasterService.updateValue(id, updateValueMasterRequest);
        return ResponseEntity.ok(updateValue);
    }

    @DeleteMapping("/deleteValue/{id}")
    public ResponseEntity<Object> deleteValue(@PathVariable Long id) throws ResourceNotFoundException {
        valueMasterService.deleteValueId(id);
        return ResponseEntity.noContent().build();
    }

}
