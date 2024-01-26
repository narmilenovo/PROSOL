package com.example.valueservice.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class PdfFileHelper extends AbstractExporter {

	private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.UNDERLINE,
			BaseColor.BLACK);
	private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD,
			BaseColor.LIGHT_GRAY);
	private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.UNDEFINED,
			BaseColor.BLACK);
	private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
	private Document document;

	public void export(HttpServletResponse response, String headerName, Class<?> clazz, String contentType,
			String extension, String prefix, List<?> list) throws IOException, DocumentException {
		super.setResponseHeader(response, contentType, extension, prefix);

		setUpDocument(response);

		document.open();

		writeTitle(headerName);
		writeHeaderLine(clazz);
		writeDataLines(list);

		document.close();
	}

	private void setUpDocument(HttpServletResponse response) throws IOException, DocumentException {
		document = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		writer.setPageEvent(new PageNumberEventHandler()); // Set the PageNumberEventHandler
	}

	private void writeTitle(String title) throws DocumentException {
		Paragraph paragraph = new Paragraph(Helpers.titleCaseWithSpace(title), TITLE_FONT);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		addEmptyLine(paragraph, 1);
		document.add(paragraph);
	}

	private void writeHeaderLine(Class<?> clazz) throws DocumentException {
		List<Field> fields = getCachedFields(clazz);
		PdfPTable table = createPdfPTable(fields.size());
		for (Field field : fields) {
			String headerName = field.getName();
			PdfPCell cell = createPdfPCell(Helpers.capitalizeWordsWithSpace(headerName), HEADER_FONT, true);
			table.addCell(cell);
		}
		document.add(table);
	}

	private void writeDataLines(List<?> list) throws DocumentException {
		List<Field> fields = getCachedFields(list.get(0).getClass());
		PdfPTable table = createPdfPTable(fields.size());
		for (Object obj : list) {
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					Object value = field.get(obj);
					PdfPCell cell = createPdfPCell(value != null ? value.toString() : "", DATA_FONT, false);
					table.addCell(cell);
				} catch (IllegalAccessException e) {
					throw new DocumentException("Error accessing field: " + field.getName(), e);
				} finally {
					field.setAccessible(false);
				}
			}
		}
		document.add(table);
	}

	private PdfPTable createPdfPTable(int columns) {
		PdfPTable table = new PdfPTable(columns);
		table.setWidthPercentage(100);
		return table;
	}

	private PdfPCell createPdfPCell(String text, Font font, boolean isHeader) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cellPadding(cell, isHeader);
		return cell;
	}

	private void cellPadding(PdfPCell cell, boolean isHeader) {
		cell.setPaddingTop(7);
		cell.setPaddingBottom(7);
		cell.setPaddingLeft(10);
		cell.setPaddingRight(10);
		cell.setUseBorderPadding(true);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(BaseColor.WHITE);
		if (isHeader) {
			cell.setBackgroundColor(BaseColor.BLACK);
		}
	}

	private List<Field> getCachedFields(Class<?> clazz) {
		return fieldCache.computeIfAbsent(clazz, FieldUtils::getAllFieldsList);
	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private static class PageNumberEventHandler extends PdfPageEventHelper {
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			int page = writer.getPageNumber();
			Phrase pageNumber = new Phrase("Page " + page);
			PdfContentByte canvas = writer.getDirectContent();
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pageNumber,
					(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}
}
