package com.example.generalservice.controller;

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.BaseUOPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BaseUOPController {
    private final BaseUOPService baseUOPService;

    @PostMapping("/saveUop")
    public ResponseEntity<Object> saveUop(@Valid @RequestBody BaseUOPRequest baseUOPRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUop").toUriString());
        BaseUOPResponse baseUOPResponse = baseUOPService.saveUop(baseUOPRequest);
        return ResponseEntity.created(uri).body(baseUOPResponse);
    }

    @GetMapping("/getAllUop")
    public ResponseEntity<Object> getAllUop() {
        List<BaseUOPResponse> allUop = baseUOPService.getAllUop();
        return ResponseEntity.ok(allUop);
    }


    @GetMapping("/getUopById/{id}")
    public ResponseEntity<Object> getUopById(@PathVariable Long id) throws ResourceNotFoundException {
        BaseUOPResponse uopResponse = baseUOPService.getUopById(id);
        return ResponseEntity.ok(uopResponse);
    }

    @GetMapping("/getAllUopTrue")
    public ResponseEntity<Object> listUopStatusTrue() {
        List<BaseUOPResponse> uopResponses = baseUOPService.findAllStatusTrue();
        return ResponseEntity.ok(uopResponses);
    }

    @PutMapping("/updateUop/{id}")
    public ResponseEntity<Object> updateUop(@PathVariable Long id, @Valid @RequestBody BaseUOPRequest updateBaseUOPRequest) throws ResourceNotFoundException, ResourceFoundException {
        BaseUOPResponse uopResponse = baseUOPService.updateUop(id, updateBaseUOPRequest);
        return ResponseEntity.ok(uopResponse);
    }

    @DeleteMapping("/deleteUop/{id}")
    public ResponseEntity<Object> deleteUop(@PathVariable Long id) throws ResourceNotFoundException {
        baseUOPService.deleteUopId(id);
        return ResponseEntity.noContent().build();
    }

}
