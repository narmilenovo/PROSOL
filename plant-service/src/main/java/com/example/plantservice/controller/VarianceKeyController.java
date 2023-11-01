package com.example.plantservice.controller;

import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.VarianceKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class VarianceKeyController {

    private final VarianceKeyService varianceKeyService;

    @PostMapping("/saveVarianceKey")
    public ResponseEntity<Object> saveVarianceKey(@Valid @RequestBody VarianceKeyRequest varianceKeyRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveVarianceKey").toUriString());
        VarianceKeyResponse savedVarianceKey = varianceKeyService.saveVarianceKey(varianceKeyRequest);
        return ResponseEntity.created(uri).body(savedVarianceKey);
    }

    @PutMapping("/updateVarianceKey/{id}")
    public ResponseEntity<Object> updateVarianceKey(@PathVariable Long id, @RequestBody VarianceKeyRequest varianceKeyRequest) throws ResourceNotFoundException, AlreadyExistsException {
        VarianceKeyResponse updateVarianceKey = varianceKeyService.updateVarianceKey(id, varianceKeyRequest);
        return ResponseEntity.ok().body(updateVarianceKey);
    }

    @GetMapping("/getVarianceKeyById/{id}")
    public ResponseEntity<Object> getVarianceKeyById(@PathVariable Long id) throws ResourceNotFoundException {
        VarianceKeyResponse foundVarianceKey = varianceKeyService.getVarianceKeyById(id);
        return ResponseEntity.ok(foundVarianceKey);
    }

    @DeleteMapping("/deleteVarianceKey/{id}")
    public ResponseEntity<String> deleteVarianceKey(@PathVariable Long id) throws ResourceNotFoundException {
        varianceKeyService.deleteVarianceKey(id);
        return ResponseEntity.ok().body("VarianceKey of '" + id + "' is deleted");
    }

    @PatchMapping("/updateVarianceKeyById/{id}")
    public ResponseEntity<Object> updateVarianceKeyStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        VarianceKeyResponse response = varianceKeyService.updateStatusUsingVarianceKeyId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusVarianceKeyId/{id}")
    public ResponseEntity<Object> updateBulkStatusVarianceKeyId(@PathVariable List<Long> id) {
        List<VarianceKeyResponse> responseList = varianceKeyService.updateBulkStatusVarianceKeyId(id);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllVarianceKey")
    public ResponseEntity<Object> getAllVarianceKey() {
        List<VarianceKeyResponse> varianceKey = varianceKeyService.getAllVarianceKey();
        return ResponseEntity.ok(varianceKey);
    }
}
