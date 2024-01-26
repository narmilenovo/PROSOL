//package com.example.generalsettings.util;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//
//public class ExcelExportUtility {
//    public static void exportExcel(HttpServletResponse response, List<String> headers, List<?> data, String sheetName) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet(sheetName);
//        createHeaderRow(sheet, headers);
//        createDataRows(sheet, data);
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=exported_data.xlsx");
//
//        try (OutputStream out = response.getOutputStream()) {
//            workbook.write(out);
//        }
//
//        workbook.close();
//    }
//
//    private static void createHeaderRow(Sheet sheet, List<String> headers) {
//        Row headerRow = sheet.createRow(0);
//        CellStyle headerCellStyle = createHeaderCellStyle(sheet.getWorkbook());
//
//        for (int i = 0; i < headers.size(); i++) {
//            Cell cell = headerRow.createCell(i);
//            String header = headers.get(i).toUpperCase();
//            cell.setCellValue(header);
//            cell.setCellStyle(headerCellStyle);
//        }
//    }
//
//    private static void createDataRows(Sheet sheet, List<?> data) {
//        // Add data to the Excel sheet based on your specific requirements
//    }
//
//    private static CellStyle createHeaderCellStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        style.setFont(font);
//        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        return style;
//    }
//}
//
//
