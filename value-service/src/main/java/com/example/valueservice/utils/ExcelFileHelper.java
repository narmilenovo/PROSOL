package com.example.valueservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.example.valueservice.exceptions.ExcelFileException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcelFileHelper extends AbstractExporter {
    private final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFCellStyle headerCellStyle;
    private XSSFCellStyle dataCellStyle;

    public void exportTemplate(HttpServletResponse response, String sheetName, Class<?> clazz, String contentType,
            String extension, String prefix) throws IOException {
        super.setResponseHeader(response, contentType, extension, prefix);
        workbook = new XSSFWorkbook();
        initializeCellStyles();
        writeHeaderLine(sheetName, clazz);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void exportData(HttpServletResponse response, String sheetName, Class<?> clazz, String contentType,
            String extension, String prefix, List<?> list) throws IOException, ExcelFileException {
        super.setResponseHeader(response, contentType, extension, prefix);
        workbook = new XSSFWorkbook();
        initializeCellStyles();
        writeHeaderLine(sheetName, clazz);
        writeDataLines(list);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }

    private void initializeCellStyles() {
        headerCellStyle = createCellStyle(true);
        dataCellStyle = createCellStyle(false);
    }

    private XSSFCellStyle createCellStyle(boolean isHeader) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        if (isHeader) {
            font.setBold(true);
            font.setColor(IndexedColors.BLUE.getIndex());
        }
        cellStyle.setFont(font);
        return cellStyle;
    }

    private void writeHeaderLine(String sheetName, Class<?> clazz) {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            XSSFRow row = sheet.createRow(0);
            List<Field> fields = getCachedFields(clazz);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                String headerName = field.getName();
                createCell(row, i, Helpers.capitalizeWordsWithSpace(headerName), headerCellStyle);
                sheet.autoSizeColumn(i); // Auto resize column width to fit data
            }
        }
    }

    private List<Field> getCachedFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, cls -> Arrays.asList(cls.getDeclaredFields()));
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Long valueLong) {
            cell.setCellValue(valueLong);
        } else if (value instanceof Boolean valueBoolean) {
            cell.setCellValue(valueBoolean);
        } else {
            cell.setCellValue(String.valueOf(value));
        }

        cell.setCellStyle(cellStyle);
    }

    @SuppressWarnings("squid:S3011")
    private void writeDataLines(List<?> list) throws ExcelFileException {
        int rowIndex = 1;
        for (Object obj : list) {
            XSSFRow row = sheet.createRow(rowIndex++);
            List<Field> fields = getCachedFields(obj.getClass());
            int columnIndex = 0;
            for (Field field : fields) {
                try {
                    field.setAccessible(true); // No longer needed
                    Object value = field.get(obj);
                    createCell(row, columnIndex++, value, dataCellStyle);
                } catch (IllegalAccessException e) {
                    throw new ExcelFileException("Error accessing field: " + field.getName(), e);
                } finally {
                    field.setAccessible(false); // Reset accessibility to its original state
                }
            }
        }
    }

    public <T> List<T> readDataFromExcel(InputStream inputStream, Class<T> clazz)
            throws IOException, ExcelFileException {
        workbook = new XSSFWorkbook(inputStream);
        sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
        List<T> dataList = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Skip the header row
                continue;
            }
            T dto = convertRowToDto(row, clazz);
            dataList.add(dto);
        }
        workbook.close();
        return dataList;
    }

    private <T> T convertRowToDto(Row row, Class<T> clazz) throws ExcelFileException {
        try {
            T dto = clazz.getDeclaredConstructor().newInstance(); // Assumes a no-argument constructor

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    // Handle null cells as needed
                    continue;
                }
                String fieldName = getFieldNameFromHeader(i); // You need to implement this method
                Method setter = findSetter(clazz, fieldName); // You need to implement this method
                if (setter != null) {
                    // Convert the cell value to the type expected by the setter
                    Class<?> parameterType = setter.getParameterTypes()[0];
                    Object cellValue = getCellValue(cell, parameterType); // You need to implement this method

                    // Invoke the setter to set the value in the DTO
                    setter.invoke(dto, cellValue);
                }
            }

            return dto;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            // Handle exceptions (e.g., log, throw, or return null)
            throw new ExcelFileException("Error converting row to DTO", e);
        }
    }

    private String getFieldNameFromHeader(int columnIndex) {
        Row headerRow = sheet.getRow(0);
        Cell headerCell = headerRow.getCell(columnIndex);
        String fieldName = headerCell.getStringCellValue();
        return Helpers.camelCaseWordsWithSpace(fieldName);
    }

    private <T> Method findSetter(Class<T> clazz, String fieldName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("set" + fieldName)) {
                return method;
            }
        }
        return null;
    }

    private Object getCellValue(Cell cell, Class<?> targetType) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (targetType == String.class) {
            return getStringCellValue(cell, cellType);
        } else if (targetType == Double.class || targetType == double.class) {
            return getNumericCellValue(cell, cellType);
        } else if (targetType == Integer.class || targetType == int.class) {
            return getIntCellValue(cell, cellType);
        } else if (targetType == Date.class) {
            return getDateCellValue(cell, cellType);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return getBooleanCellValue(cell, cellType);
        } else {
            // Add more type conversions as needed
            return null;
        }
    }

    private Object getStringCellValue(Cell cell, CellType cellType) {
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return String.valueOf(cell);
        }
    }

    private Object getNumericCellValue(Cell cell, CellType cellType) {
        if (cellType == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            return null; // Handle non-numeric types as needed
        }
    }

    private Object getIntCellValue(Cell cell, CellType cellType) {
        if (cellType == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else {
            return null; // Handle non-numeric types as needed
        }
    }

    private Object getDateCellValue(Cell cell, CellType cellType) {
        if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            return null; // Handle non-date or non-numeric types as needed
        }
    }

    private Object getBooleanCellValue(Cell cell, CellType cellType) {
        if (cellType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else {
            return null; // Handle non-boolean types as needed
        }
    }

}
