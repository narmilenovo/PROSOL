package com.example.valueservice.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LocalDateTimeDeserializer extends JsonDeserializer<Date> {
    private static final List<String> DATE_FORMATS = Arrays.asList(
            "yyyy-MM-dd'T'HH:mm:ss",
            "dd-MM-yyyy hh:mm:ss a z");

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText();
        for (String dateFormat : DATE_FORMATS) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                return formatter.parse(dateStr);
            } catch (ParseException ignored) {
                // Try the next format
            }
        }

        throw new RuntimeException("Unable to parse date: " + dateStr);
    }
}

