package com.example.generalsettings.service;

import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.entity.Hsn;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExcelParserHsnService {
    void saveDataToDatabase(MultipartFile file)throws AlreadyExistsException, IOException;

    List<String> getHeadersFromEntity();

    void exportEmptyExcel(HttpServletResponse response, List<String> headers);
    public List<Hsn> findAll();

    List<Map<String, Object>> convertHsnListToMap(List<Hsn> hsnS);
}
