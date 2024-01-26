package com.example.generalsettings.config;



import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.context.annotation.Configuration;


import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Map;


@Configuration
public class GeneratePdfReport {
    boolean firstRow = true;
    public byte[] generateGenericPdfReport(List<Map<String, Object>> data, String fileName) {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.addTitle(fileName);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        PdfPTable table = new PdfPTable(data.get(0).size());
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);



        PdfPCell idHeaderCell = new PdfPCell(new Phrase("Id", headFont));
        idHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(idHeaderCell);

        for (String fieldName : data.get(0).keySet()) {
            if (!"Id".equals(fieldName)) {
                PdfPCell hCell = new PdfPCell(new Phrase(fieldName, headFont));
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(hCell);
            }
        }


        for (Map<String, Object> entityData : data) {
            if (firstRow) {
                Object idValue = entityData.get("Id");
                PdfPCell idCell = new PdfPCell(new Phrase(idValue.toString()));
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);
                firstRow = false;
            }

            for (Map.Entry<String, Object> fieldName : entityData.entrySet()) {
                if (!"Id".equals(fieldName.getKey())) {
                    Object value = fieldName.getValue();
                    if (value != null) {
                        PdfPCell cell = new PdfPCell(new Phrase(value.toString()));
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                }
            }
        }

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
