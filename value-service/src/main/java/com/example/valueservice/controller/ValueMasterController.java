package com.example.valueservice.controller;

import com.example.valueservice.client.AttributeUomResponse;
import com.example.valueservice.client.SettingsClient;
import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceFoundException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.service.interfaces.ValueMasterService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ValueMasterController {

    private final ValueMasterService valueMasterService;
    private final SettingsClient settingsClient;

    @PostMapping("/saveValue")
    public ResponseEntity<Object> saveValue(@Valid @RequestBody ValueMasterRequest valueMasterRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValue").toUriString());
        ValueMasterResponse savedValue = valueMasterService.saveValue(valueMasterRequest);
        return ResponseEntity.created(uri).body(savedValue);
    }

    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ExcelFileException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploadFile").toUriString());
        valueMasterService.uploadData(file);
        return ResponseEntity.created(uri).build();
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

    @GetMapping("/downloadTemplate/value")
    public void excelValueTemplate(HttpServletResponse httpServletResponse) throws IOException {
        valueMasterService.downloadTemplate(httpServletResponse);
    }

    @GetMapping("/export/AllData")
    public void excelAll(HttpServletResponse httpServletResponse) throws IOException, ExcelFileException {
        valueMasterService.downloadAllData(httpServletResponse);
    }

    @GetMapping("/exportPdf/AllData")
    public void exportPdf(HttpServletResponse httpServletResponse) throws IOException, IllegalAccessException, ExcelFileException, DocumentException {
        valueMasterService.exportPdf(httpServletResponse);
    }

    @GetMapping("/getAttributeUomById/{id}")
    public ResponseEntity<Object> getAttributeUomById(@PathVariable Long id) {
        return ResponseEntity.ok(settingsClient.getAttributeUomById(id));
    }

    @GetMapping(value = "/getAllAttributeUom/{uomId}", produces = "application/json")
    public AttributeUomResponse getUomById(@PathVariable Long uomId) {
        return settingsClient.getUomById(uomId);
    }

}
