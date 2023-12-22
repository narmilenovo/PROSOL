package com.example.valueservice.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.BadRequestResponse;
import com.example.valueservice.dto.response.InvalidDataResponse;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceFoundException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.service.interfaces.ValueMasterService;
import com.itextpdf.text.DocumentException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ValueMasterController {

        private final ValueMasterService valueMasterService;

        @Operation(summary = "Save Value", responses = {
                        @ApiResponse(responseCode = "201", description = "Value saved successfully", content = {
                                        @Content(schema = @Schema(implementation = ValueMasterResponse.class))
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "422", description = "Invalid data", content = {
                                        @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                        })
        })
        @PostMapping("/saveValue")
        public ResponseEntity<Object> saveValue(@Valid @RequestBody ValueMasterRequest valueMasterRequest)
                        throws ResourceFoundException {
                URI uri = URI.create(
                                ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValue").toUriString());
                ValueMasterResponse savedValue = valueMasterService.saveValue(valueMasterRequest);
                return ResponseEntity.created(uri).body(savedValue);
        }

        @Operation(summary = "Upload File", responses = {
                        @ApiResponse(responseCode = "201", description = "File uploaded successfully"),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "422", description = "Invalid data", content = {
                                        @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                        })
        })
        @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
        public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file)
                        throws IOException, ExcelFileException {
                URI uri = URI.create(
                                ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploadFile").toUriString());
                valueMasterService.uploadData(file);
                return ResponseEntity.created(uri).build();
        }

        @Operation(summary = "Get All Values", responses = {
                        @ApiResponse(responseCode = "200", description = "Values retrieved successfully", content = {
                                        @Content(schema = @Schema(implementation = ValueMasterResponse.class))
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @GetMapping("/getAllValue")
        public ResponseEntity<Object> getAllValue(@RequestParam(required = false) boolean attributeUom) {
                List<?> allValue = attributeUom ? valueMasterService.getAllValueAttributeUom()
                                : valueMasterService.getAllValue(false);
                return ResponseEntity.ok(allValue);
        }

        @Operation(summary = "Get Value By Id", responses = {
                        @ApiResponse(responseCode = "200", description = "Value retrieved successfully", content = {
                                        @Content(schema = @Schema(implementation = ValueMasterResponse.class))
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @GetMapping("/getValueById/{id}")
        public ResponseEntity<Object> getValueById(@PathVariable Long id,
                        @RequestParam(required = false) boolean attributeUom) throws ResourceNotFoundException {
                Object valueById = null;
                if (attributeUom) {
                        valueById = valueMasterService.getValueAttributeUomById(id);
                }
                if (!attributeUom) {

                        valueById = valueMasterService.getValueById(id);
                } else {
                        valueById = valueMasterService.getValueById(id);
                }
                return ResponseEntity.ok(valueById);
        }

        @Operation(summary = "Update Value", responses = {
                        @ApiResponse(responseCode = "200", description = "Value updated successfully", content = {
                                        @Content(schema = @Schema(implementation = ValueMasterResponse.class))
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "422", description = "Invalid data", content = {
                                        @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @PutMapping("/updateValue/{id}")
        public ResponseEntity<Object> updateValue(@PathVariable Long id,
                        @Valid @RequestBody ValueMasterRequest updateValueMasterRequest)
                        throws ResourceNotFoundException, ResourceFoundException {
                ValueMasterResponse updateValue = valueMasterService.updateValue(id, updateValueMasterRequest);
                return ResponseEntity.ok(updateValue);
        }

        @Operation(summary = "Delete Value", responses = {
                        @ApiResponse(responseCode = "204", description = "Value deleted successfully"),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @DeleteMapping("/deleteValue/{id}")
        public ResponseEntity<Object> deleteValue(@PathVariable Long id) throws ResourceNotFoundException {
                valueMasterService.deleteValueId(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Download Template", responses = {
                        @ApiResponse(responseCode = "200", description = "Template downloaded successfully"),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @GetMapping("/downloadTemplate/value")
        public void excelValueTemplate(HttpServletResponse httpServletResponse) throws IOException {
                valueMasterService.downloadTemplate(httpServletResponse);
        }

        @Operation(summary = "Download All Data Excel", responses = {
                        @ApiResponse(responseCode = "200", description = "All data downloaded successfully"),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @GetMapping("/export/AllData")
        public void excelAll(HttpServletResponse httpServletResponse) throws IOException, ExcelFileException {
                valueMasterService.downloadAllData(httpServletResponse);
        }

        @Operation(summary = "Download All Data Pdf", responses = {
                        @ApiResponse(responseCode = "200", description = "All data downloaded successfully"),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "You dont have access to this resource", content = {
                                        @Content(schema = @Schema(implementation = BadRequestResponse.class))
                        })
        })
        @GetMapping("/exportPdf/AllData")
        public void exportPdf(HttpServletResponse httpServletResponse)
                        throws IOException, IllegalAccessException, ExcelFileException, DocumentException {
                valueMasterService.exportPdf(httpServletResponse);
        }
}
