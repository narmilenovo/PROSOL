package com.example.generalsettings.serviceimpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;


import com.example.generalsettings.config.AlreadyExistsException;

import com.example.generalsettings.repo.AttributeUomRepo;
import com.example.generalsettings.util.ExcelUploadService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.service.ExcelParserService;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class ExcelParserServiceImpl implements ExcelParserService{
    private final AttributeUomRepo attributeUomRepo;


    @Override
    public void saveDataToDatabase(MultipartFile file) throws AlreadyExistsException ,IOException{
        if (ExcelUploadService.isValidExcelFile(file)) {

            List<AttributeUom> attributeUomS = ExcelUploadService.getDataFromExcel(file.getInputStream());
            for (AttributeUom uom : attributeUomS) {
                if (!attributeUomRepo.existsByAttributeUomName(uom.getAttributeUomName())) {
                    this.attributeUomRepo.save(uom);
                } else {
                    throw new AlreadyExistsException("Already UOM Name is Present");

                }
            }
        }
    }
@Override
    public void exportEmptyExcel(HttpServletResponse response,List<String> headers)throws RuntimeException {
        try {
            ExcelUploadService.exportExcel(response,headers);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error exporting the Excel file");
        } catch (Exception e) {
            e.getStackTrace();
        }
}
    public List<String> getHeadersFromEntity() {
        Pattern pattern = Pattern.compile("(?=[A-Z][a-z])");
        List<String> headers = new ArrayList<>();
        Class<AttributeUom> entityClass = AttributeUom.class;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            String[] words = pattern.split(field.getName());
            String header = String.join(" ", words);
            headers.add(header);
        }
        return headers;
    }

    @Override
    public List<AttributeUom> findAll() {
        return attributeUomRepo.findAll();
    }

    @Override
    public List<Map<String, Object>> convertUomListToMap(List<AttributeUom> hsnList) {
        List<Map<String, Object>> data = new ArrayList<>();

        for (AttributeUom uom : hsnList) {
            Map<String, Object> hsnData = new HashMap<>();
            hsnData.put("Id", uom.getId());
            hsnData.put("UOMCode", uom.getAttributeUomCode());
            hsnData.put("UOMDesc", uom.getAttributeUomName());
            hsnData.put("UOMStatus", uom.getAttributeUomStatus());
            data.add(hsnData);
        }
        return data;
    }

}
