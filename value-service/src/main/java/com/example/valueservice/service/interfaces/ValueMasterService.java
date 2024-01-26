package com.example.valueservice.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.valueservice.client.ValueAttributeUom;
import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

public interface ValueMasterService {
	ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest) throws ResourceNotFoundException;

	ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException;

	ValueAttributeUom getValueAttributeUomById(Long id) throws ResourceNotFoundException;

	List<ValueMasterResponse> getAllValue(boolean attributeUom) throws ResourceNotFoundException;

	List<ValueAttributeUom> getAllValueAttributeUom() throws ResourceNotFoundException;

	ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest)
			throws ResourceNotFoundException;

	void deleteValueId(Long id) throws ResourceNotFoundException;

	void deleteBatchValue(List<Long> ids) throws ResourceNotFoundException;

	void downloadTemplate(HttpServletResponse httpServletResponse) throws IOException;

	void uploadData(MultipartFile file) throws IOException, ExcelFileException;

	void downloadAllData(HttpServletResponse httpServletResponse)
			throws IOException, ExcelFileException, ResourceNotFoundException;

	void exportPdf(HttpServletResponse httpServletResponse)
			throws IOException, IllegalAccessException, ExcelFileException, DocumentException,
			ResourceNotFoundException;

}
