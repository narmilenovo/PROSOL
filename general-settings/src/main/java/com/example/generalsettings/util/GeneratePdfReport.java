package com.example.generalsettings.util;



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
    public byte[] generateGenericPdfReport(List<Map<String, Object>> data, String fileName) {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.addTitle(fileName);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        PdfPTable table = new PdfPTable(data.get(0).size());


        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for (String fieldName : data.get(0).keySet()) {
            PdfPCell hCell = new PdfPCell(new Phrase(fieldName, headFont));
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hCell);
        }


        for (Map<String, Object> entityData : data) {
            for (String fieldName : entityData.keySet()) {
                PdfPCell cell = new PdfPCell(new Phrase(entityData.get(fieldName).toString()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}