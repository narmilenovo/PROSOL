package com.example.batchservice.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExcelFileHelper extends AbstractExporter {

	private final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private XSSFCellStyle headerCellStyle;
	private DataValidationHelper validationHelper;

	public void exportTemplate(HttpServletResponse response, String sheetName, Class<?> clazz, String contentType,
			String extension, String prefix) throws IOException {
		super.setResponseHeader(response, contentType, extension, prefix);
		workbook = new XSSFWorkbook();
		initializeCellStyles();
		writeHeaderLine(sheetName, clazz);
		workbook.write(response.getOutputStream());
		workbook.close();
	}

	private void initializeCellStyles() {
		headerCellStyle = createCellStyle(true);
		validationHelper = new XSSFDataValidationHelper(sheet);
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
		if (sheetName != null) {
			sheet = workbook.getSheet(sheetName);
		}
		sheet = workbook.createSheet(sheetName);
		XSSFRow row = sheet.createRow(0);
		List<Field> classFields = getCachedFields(clazz);
		for (int i = 0; i < classFields.size(); i++) {
			Field field = classFields.get(i);
			String headerName = Helpers.capitalizeEachWord(field.getName());
			createCell(row, i, headerName);
			sheet.autoSizeColumn(i);
			if (field.getName().equals("plantStatus")) {
				// If the field is "plantStatus", add Excel validation for TRUE/FALSE dropdown
				addStatusValidation(sheet, 1, i, i); // Adjust lastCol parameter
			}
		}
	}

	private void addStatusValidation(XSSFSheet sheet, int firstRow, int firstCol, int lastCol) {
		int lastRow = sheet.getLastRowNum() - 1;
		if (firstRow <= lastRow && firstCol <= lastCol) {
			validationHelper = sheet.getDataValidationHelper();
			DataValidationConstraint constraint = validationHelper
					.createExplicitListConstraint(new String[] { "TRUE", "FALSE" });
			CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
			DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

			dataValidation.createPromptBox("Plant Status", "Please select TRUE or FALSE from the dropdown list.");
			dataValidation.setShowPromptBox(true);

			dataValidation.createErrorBox("Invalid Value", "Please enter either TRUE or FALSE.");
			dataValidation.setShowErrorBox(true);

			dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);

			dataValidation.setEmptyCellAllowed(false);
			sheet.addValidationData(dataValidation);

		}
	}

	private List<Field> getCachedFields(Class<?> clazz) {
		return fieldCache.computeIfAbsent(clazz, cls -> Arrays.asList(cls.getDeclaredFields()));
	}

	private void createCell(XSSFRow row, int columnIndex, String value) {
		XSSFCell cell = row.createCell(columnIndex);
		cell.setCellValue(value);
		cell.setCellStyle(headerCellStyle);
	}

}
