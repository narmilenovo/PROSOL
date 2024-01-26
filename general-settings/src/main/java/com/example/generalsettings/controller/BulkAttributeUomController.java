package com.example.generalsettings.controller;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.AttributeUom;
import org.springframework.http.HttpHeaders;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.config.GeneratePdfReport;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.generalsettings.service.ExcelParserService;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
public class BulkAttributeUomController {

	private final ExcelParserService excelParserService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping(value = "/upload-data",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Object> uploadData(@RequestPart("file") MultipartFile file) throws AlreadyExistsException ,IOException{
		this.excelParserService.saveDataToDatabase(file);
		return ResponseEntity
				.ok(Map.of("Message", " Data uploaded and saved to database successfully"));
	}

	@GetMapping("/exportData")
	public ResponseEntity<Object> exportExcel(HttpServletResponse response)  {
		List<String> headers = excelParserService.getHeadersFromEntity();
		this.excelParserService.exportEmptyExcel(response,headers);
		return ResponseEntity.ok("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfUomReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateHsnReport() {
		List<AttributeUom> uom = excelParserService.findAll();
		List<Map<String, Object>> data = excelParserService.convertUomListToMap(uom);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "UomReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "UOMReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}


}