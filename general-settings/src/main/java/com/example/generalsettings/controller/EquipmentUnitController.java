package com.example.generalsettings.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.generalsettings.config.GeneratePdfReport;
import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;
import com.example.generalsettings.service.EquipmentUnitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EquipmentUnitController {
	private final EquipmentUnitService equipmentUnitService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveEquipmentUnit")
	public ResponseEntity<Object> saveEquipmentUnit(@Valid @RequestBody EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveEquipmentUnit").toUriString());
		EquipmentUnitResponse savedEquipmentUnit = equipmentUnitService.saveEquipmentUnit(equipmentUnitRequest);
		return ResponseEntity.created(uri).body(savedEquipmentUnit);
	}

	@GetMapping("/getEquipmentUnitById/{id}")
	public ResponseEntity<Object> getEquipmentUnitById(@PathVariable Long id) throws ResourceNotFoundException {
		EquipmentUnitResponse foundEquipmentUnit = equipmentUnitService.getEquipmentUnitById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundEquipmentUnit);
	}

	@GetMapping("/getAllEquipmentUnit")
	public ResponseEntity<Object> getAllEquipmentUnit() {
		List<EquipmentUnitResponse> equipmentUnit = equipmentUnitService.getAllEquipmentUnit();
		return ResponseEntity.ok(equipmentUnit);
	}

	@PutMapping("/updateEquipmentUnit/{id}")
	public ResponseEntity<Object> updateEquipmentUnit(@PathVariable Long id,
			@RequestBody EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		EquipmentUnitResponse updateEquipmentUnit = equipmentUnitService.updateEquipmentUnit(id, equipmentUnitRequest);
		return ResponseEntity.ok().body(updateEquipmentUnit);
	}

	@PatchMapping("/updateEquipmentUnitById/{id}")
	public ResponseEntity<Object> updateEquipmentUnitStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		EquipmentUnitResponse response = equipmentUnitService.updateStatusUsingEquipmentUnitId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusEquipmentUnitId")
	public ResponseEntity<Object> updateBulkStatusEquipmentUnitId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<EquipmentUnitResponse> responseList = equipmentUnitService.updateBulkStatusEquipmentUnitId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteEquipmentUnit/{id}")
	public ResponseEntity<String> deleteEquipmentUnit(@PathVariable Long id) throws ResourceNotFoundException {
		equipmentUnitService.deleteEquipmentUnit(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchEquipmentUnit")
	public ResponseEntity<Object> deleteBatchEquipmentUnit(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		equipmentUnitService.deleteBatchEquipmentUnit(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfEquipmentUnitReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<EquipmentUnit> equipmentUnitReport = equipmentUnitService.findAll();
		List<Map<String, Object>> data = equipmentUnitService.convertEquipmentUnitListToMap(equipmentUnitReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "EquipmentUnitReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "EquipmentUnitReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
