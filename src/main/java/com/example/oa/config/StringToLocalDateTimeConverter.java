package com.example.oa.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom converter to handle both date and datetime formats for LocalDateTime parameters.
 * Supports:
 * - yyyy-MM-dd'T'HH:mm:ss (ISO datetime)
 * - yyyy-MM-dd (ISO date, converted to start of day)
 */
@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        
        try {
            // Try parsing as full datetime first
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e1) {
            try {
                // Try parsing as date only, convert to start of day
                LocalDate date = LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE);
                return date.atStartOfDay();
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss, got: " + source);
            }
        }
    }
}
