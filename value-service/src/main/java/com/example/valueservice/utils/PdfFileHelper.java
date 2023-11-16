package com.example.valueservice.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PdfFileHelper extends AbstractExporter {

    private final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
    Document document;

    public void export(HttpServletResponse response, String headerName, Class<?> clazz, String contentType, String extension, String prefix, List<?> list) throws IllegalAccessException, IOException, DocumentException {
        super.setResponseHeader(response, contentType, extension, prefix);
        Rectangle pageSize = PageSize.A3;
        document = new Document(pageSize);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new PageNumberEventHandler());  // Set the PageNumberEventHandler
        document.open();

        writeTitle(headerName);
        writeHeaderLine(clazz);
        writeDataLines(list);

        document.close();
    }

    private void writeTitle(String title) throws DocumentException {
        Paragraph paragraph = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(paragraph, 1);
        document.add(paragraph);
    }

    private void writeHeaderLine(Class<?> clazz) throws DocumentException {
        List<Field> fields = getCachedFields(clazz);

        PdfPTable table = new PdfPTable(fields.size());
        table.setWidthPercentage(100);
        // Set RGB values for blue color (you can adjust these values as needed)
        int red = 255;
        int green = 255;
        int blue = 255;

        // Create a BaseColor with the specified RGB values
        BaseColor blueColor = new BaseColor(red, green, blue);

        // Create a Font with Helvetica-Bold, size 12, and the specified blue color
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.UNDEFINED, blueColor);

//        // Set the RGB color using HEX value (e.g., #0000FF for blue)
//        BaseColor blueColor = new BaseColor(Integer.parseInt("0000FF", 16));
//
//        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, blueColor);

        for (Field field : fields) {
            String headerName = field.getName();
            PdfPCell cell = new PdfPCell(new Phrase(Helpers.capitalizeWordsWithSpace(headerName), headerFont));
            // Set horizontal alignment to center
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setUseBorderPadding(true);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void writeDataLines(List<?> list) throws DocumentException, IllegalAccessException {
        List<Field> fields = getCachedFields(list.get(0).getClass());

        PdfPTable table = new PdfPTable(fields.size());
        table.setWidthPercentage(100);

        for (Object obj : list) {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : ""));
                // Set horizontal alignment to center
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseBorderPadding(true);
                table.addCell(cell);
            }
        }

        document.add(table);
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
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}
