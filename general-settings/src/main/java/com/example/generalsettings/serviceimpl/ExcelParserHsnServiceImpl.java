package com.example.generalsettings.serviceimpl;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.repo.HsnRepo;
import com.example.generalsettings.service.ExcelParserHsnService;
import com.example.generalsettings.util.ExcelUploadHsn;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
public class ExcelParserHsnServiceImpl implements ExcelParserHsnService {
    private final HsnRepo hsnRepo;

    @Override
    public void saveDataToDatabase(MultipartFile file) throws AlreadyExistsException, IOException {
        if (ExcelUploadHsn.isValidExcelFile(file)) {

            List<Hsn> hsnS = ExcelUploadHsn.getDataFromExcel(file.getInputStream());
            for (Hsn hsn : hsnS) {
                if (!hsnRepo.existsByHsnDesc(hsn.getHsnDesc())) {
                    this.hsnRepo.save(hsn);
                } else {
                    throw new AlreadyExistsException("Already HSN Name is Present");
                }
            }
        }
    }
    @Override
    public void exportEmptyExcel(HttpServletResponse response, List<String> headers) {
        try {
            ExcelUploadHsn.exportExcel(response,headers);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error exporting the Excel file");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
    public List<String> getHeadersFromEntity() {
        Pattern pattern = Pattern.compile("(?=[A-Z][a-z])");
        List<String> headers = new ArrayList<>();
        Class<Hsn> entityClass = Hsn.class;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            String[] words = pattern.split(field.getName());
            String header = String.join(" ", words);
            headers.add(header);
        }
        return headers;
    }

    @Override
    public List<Hsn> findAll() {
        return hsnRepo.findAll();
    }

    public List<Map<String, Object>> convertHsnListToMap(List<Hsn> hsnList) {
        List<Map<String, Object>> data = new ArrayList<>();

        for (Hsn hsn : hsnList) {
            Map<String, Object> hsnData = new HashMap<>();
            hsnData.put("Id", hsn.getId());
            hsnData.put("HsnCode", hsn.getHsnCode());
            hsnData.put("HsnDesc", hsn.getHsnDesc());
            hsnData.put("HsnStatus", hsn.getHsnStatus());
            data.add(hsnData);
        }
        return data;
    }
}
