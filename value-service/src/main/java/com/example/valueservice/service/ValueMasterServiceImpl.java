package com.example.valueservice.service;

import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.entity.ValueMaster;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.repository.ValueMasterRepository;
import com.example.valueservice.service.interfaces.ValueMasterService;
import com.example.valueservice.utils.ExcelFileHelper;
import com.example.valueservice.utils.PdfFileHelper;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValueMasterServiceImpl implements ValueMasterService {

    private final ValueMasterRepository valueMasterRepository;
    private final ModelMapper modelMapper;
    @Lazy
    private final ExcelFileHelper excelFileHelper;
    private final PdfFileHelper pdfFileHelper;

    @Override
    public ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest) {
        ValueMaster valueMaster = modelMapper.map(valueMasterRequest, ValueMaster.class);
        ValueMaster savedValue = valueMasterRepository.save(valueMaster);
        return mapToValueMasterResponse(savedValue);
    }

    @Override
    public List<ValueMasterResponse> getAllValue() {
        List<ValueMaster> allValues = valueMasterRepository.findAll();
        return allValues.stream()
                .sorted(Comparator.comparing(ValueMaster::getId))
                .map(this::mapToValueMasterResponse)
                .toList();
    }

    @Override
    public ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException {
        ValueMaster valueMaster = findValueById(id);
        return mapToValueMasterResponse(valueMaster);
    }

    @Override
    public ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest) throws ResourceNotFoundException {
        ValueMaster existingValueMaster = findValueById(id);
        modelMapper.map(updateValueMasterRequest, existingValueMaster);
        ValueMaster updatedValueMaster = valueMasterRepository.save(existingValueMaster);
        return mapToValueMasterResponse(updatedValueMaster);
    }

    @Override
    public void deleteValueId(Long id) throws ResourceNotFoundException {
        ValueMaster valueMaster = findValueById(id);
        valueMasterRepository.deleteById(valueMaster.getId());
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String sheetName = "ValueMaster";
        Class<?> clazz = ValueMasterRequest.class;
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String extension = ".xlsx";
        String prefix = "ValueMaster_";
        excelFileHelper.exportTemplate(response, sheetName, clazz, contentType, extension, prefix);
    }

    @Override
    public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
        String sheetName = "ValueMaster";
        Class<?> clazz = ValueMasterResponse.class;
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String extension = ".xlsx";
        String prefix = "ValueMaster_";
        List<ValueMasterResponse> allValues = getAllValue();
        excelFileHelper.exportData(response, sheetName, clazz, contentType, extension, prefix, allValues);
    }

    @Override
    public void exportPdf(HttpServletResponse response) throws IOException, IllegalAccessException, ExcelFileException, DocumentException {
        String headerName = "List Of Values";
        Class<?> clazz = ValueMasterResponse.class;
        String contentType = "application/pdf";
        String extension = ".pdf";
        String prefix = "ValueMaster_";
        List<ValueMasterResponse> allValues = getAllValue();
        pdfFileHelper.export(response, headerName, clazz, contentType, extension, prefix, allValues);
    }

    @Override
    public void uploadData(MultipartFile file) throws IOException, ExcelFileException {
        List<ValueMasterRequest> fromExcel = excelFileHelper.readDataFromExcel(file.getInputStream(), ValueMasterRequest.class);
        List<ValueMaster> valueMasters = modelMapper.map(fromExcel, new TypeToken<List<ValueMaster>>() {
        }.getType());
        valueMasters.forEach(valueMaster -> valueMaster.setId(null));
        valueMasterRepository.saveAll(valueMasters);
    }


    private ValueMaster findValueById(Long id) throws ResourceNotFoundException {
        Optional<ValueMaster> valueMaster = valueMasterRepository.findById(id);
        if (valueMaster.isEmpty()) {
            throw new ResourceNotFoundException("Value Master with this ID Not found");
        }
        return valueMaster.get();
    }


    private ValueMasterResponse mapToValueMasterResponse(ValueMaster valueMaster) {
        return modelMapper.map(valueMaster, ValueMasterResponse.class);
    }

}
