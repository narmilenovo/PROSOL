package com.example.vendor_masterservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.BadRequestResponse;
import com.example.vendor_masterservice.dto.response.InvalidDataResponse;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Vendor Master", description = "Vendor Master Service API")
@RestController
@RequiredArgsConstructor
public class VendorMasterController {

	private final VendorMasterService vendorMasterService;

	@Operation(summary = "Save Vendor Master", responses = {
			@ApiResponse(responseCode = "201", description = "Vendor Master saved successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to save Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/saveVm")
	public ResponseEntity<Object> saveVm(@RequestBody VendorMasterRequest vendorMasterRequest)
			throws ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveVm").toUriString());
		VendorMasterResponse savedVm = vendorMasterService.saveVm(vendorMasterRequest);
		return ResponseEntity.created(uri).body(savedVm);
	}

	@Operation(summary = "Save All Vendor Master", responses = {
			@ApiResponse(responseCode = "201", description = "Vendor Master saved successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to save Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/saveAllVm")
	public ResponseEntity<Object> saveAllVm(@Valid @RequestBody List<VendorMasterRequest> vendorMasterRequests) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAllVm").toUriString());
		List<VendorMasterResponse> savedAllVm = vendorMasterService.saveAllVm(vendorMasterRequests);
		return ResponseEntity.created(uri).body(savedAllVm);
	}

	@Operation(summary = "Get Vendor Master By Id", responses = {
			@ApiResponse(responseCode = "200", description = "Vendor Master fetched successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to fetch Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@GetMapping("/getVmById/{id}")
	public ResponseEntity<Object> getVmById(@PathVariable Long id) throws ResourceNotFoundException {
		VendorMasterResponse vmById = vendorMasterService.getVmById(id);
		return ResponseEntity.status(HttpStatus.OK).body(vmById);
	}

	@Operation(summary = "Get All Vendor Master", responses = {
			@ApiResponse(responseCode = "200", description = "Vendor Master fetched successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to fetch Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@GetMapping("/getAllVm")
	public ResponseEntity<Object> getAllVm() {
		List<VendorMasterResponse> allVm = vendorMasterService.getAllVm();
		return ResponseEntity.ok(allVm);
	}

	@Operation(summary = "Get Vendor Master By Status True", responses = {
			@ApiResponse(responseCode = "200", description = "Vendor Master fetched successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to fetch Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@GetMapping("/getAllVmTrue")
	public ResponseEntity<Object> listVmStatusTrue() {
		List<VendorMasterResponse> masterResponses = vendorMasterService.findAllStatusTrue();
		return ResponseEntity.ok(masterResponses);
	}

	@Operation(summary = "Update Vendor Master", responses = {
			@ApiResponse(responseCode = "200", description = "Vendor Master updated successfully", content = {
					@Content(schema = @Schema(implementation = VendorMasterResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to update Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PutMapping("/updateVm/{id}")
	public ResponseEntity<Object> updateVm(@PathVariable Long id,
			@RequestBody VendorMasterRequest updateVendorMasterRequest) throws ResourceNotFoundException {
		VendorMasterResponse masterResponse = vendorMasterService.updateVm(id, updateVendorMasterRequest);
		return ResponseEntity.ok(masterResponse);
	}

	@PatchMapping("/updateVmStatusById/{id}")
	public ResponseEntity<Object> updateVmStatusById(@PathVariable Long id) throws ResourceNotFoundException {
		VendorMasterResponse response = vendorMasterService.updateVmStatusById(id);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/updateBulkStatusVmId")
	public ResponseEntity<Object> updateBulkStatusVmId(@RequestBody @NonNull List<Long> id) throws ResourceNotFoundException {
		List<VendorMasterResponse> responseList = vendorMasterService.updateBulkStatusVmId(id);
		return ResponseEntity.ok(responseList);
	}

	@Operation(summary = "Delete Vendor Master", responses = {
			@ApiResponse(responseCode = "204", description = "Vendor Master deleted successfully", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Failed to delete Vendor data", content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = "Invalid data", content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@DeleteMapping("/deleteVm/{id}")
	public ResponseEntity<Object> deleteVm(@PathVariable Long id) throws ResourceNotFoundException {
		vendorMasterService.deleteVmId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteVmBatchById")
	public ResponseEntity<Object> deleteVmBatchById(@RequestBody @NonNull List<Long> id) throws ResourceNotFoundException {
		vendorMasterService.deleteVmBatchById(id);
		return ResponseEntity.noContent().build();
	}

}
