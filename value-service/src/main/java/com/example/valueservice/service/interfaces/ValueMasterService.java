package com.example.valueservice.service.interfaces;

import com.example.valueservice.client.ValueAttributeUom;
import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ValueMasterService {
    ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest);

    List<ValueMasterResponse> getAllValue(boolean attributeUom);

    ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException;

    ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest) throws ResourceNotFoundException;

    void deleteValueId(Long id) throws ResourceNotFoundException;


    void downloadTemplate(HttpServletResponse httpServletResponse) throws IOException;

    void downloadAllData(HttpServletResponse httpServletResponse) throws IOException, ExcelFileException;

    void uploadData(MultipartFile file) throws IOException, ExcelFileException;

    void exportPdf(HttpServletResponse httpServletResponse) throws IOException, IllegalAccessException, ExcelFileException, DocumentException;

    List<ValueAttributeUom> getAllValueAttributeUom();

    ValueAttributeUom getValueAttributeUomById(Long id) throws ResourceNotFoundException;
}
