package com.example.generalsettings.controller;



import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.service.ExcelParserHsnService;
import com.example.generalsettings.util.GeneratePdfReport;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class BulkHsnController {

    private final ExcelParserHsnService excelParserHsnService;
    private final GeneratePdfReport generatePdfReport;

    @PostMapping(value = "/upload-data1",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> uploadData(@RequestPart("file") MultipartFile file) throws AlreadyExistsException, IOException {
        this.excelParserHsnService.saveDataToDatabase(file);
        return ResponseEntity
                .ok(Map.of("Message", " Data uploaded and saved to database successfully"));
    }

    @GetMapping("/exportData1")
    public ResponseEntity<Object> exportExcel(HttpServletResponse response)  {
        List<String> headers = excelParserHsnService.getHeadersFromEntity();
        this.excelParserHsnService.exportEmptyExcel(response,headers);
        return ResponseEntity.ok("Excel file exported successfully");
    }


    @GetMapping(value = "/pdfreport", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateHsnReport()   {
        List<Hsn> hsnS = excelParserHsnService.findAll();
        List<Map<String, Object>> data = excelParserHsnService.convertHsnListToMap(hsnS);
        byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "HsnReport.pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
        headers.setContentDispositionFormData("attachment", "HsnReport.pdf");
        return ResponseEntity.ok().headers(headers).body(pdfContents);
    }


}
