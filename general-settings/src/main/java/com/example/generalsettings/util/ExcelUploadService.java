package com.example.generalsettings.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.example.generalsettings.entity.AttributeUom;

import jakarta.servlet.http.HttpServletResponse;

public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<AttributeUom> getDataFromExcel(InputStream inputStream) {
        List<AttributeUom> attributeUomS = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheet("attributeUomS");

            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                AttributeUom attributeUom = new AttributeUom();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cellIndex) {
                        case 0 -> attributeUom.setId(attributeUom.getId());
                        case 1 -> attributeUom.setAttributeUnit(cell.getStringCellValue());
                        case 2 -> attributeUom.setAttributeUomName(cell.getStringCellValue());
                        case 3 -> attributeUom.setAttributeUomStatus(cell.getBooleanCellValue());
                        default -> {
                            break;
                        }
                    }
                    cellIndex++;
                }
                attributeUomS.add(attributeUom);
                // Remove the line below since it's not needed and may cause issues
                attributeUomS.remove(attributeUom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attributeUomS;
    }

    public static void exportExcel(HttpServletResponse response, List<String> headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("attributeUomS");
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = createHeaderCellStyle(workbook);

        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            String header = headers.get(i).toUpperCase();
            cell.setCellValue(header);
            cell.setCellStyle(headerCellStyle);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=empty_excel.xlsx");

        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }

        workbook.close();
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
