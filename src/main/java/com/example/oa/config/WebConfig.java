package com.example.oa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for custom converters and formatters.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToLocalDateTimeConverter stringToLocalDateTimeConverter;

    public WebConfig(StringToLocalDateTimeConverter stringToLocalDateTimeConverter) {
        this.stringToLocalDateTimeConverter = stringToLocalDateTimeConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToLocalDateTimeConverter);
    }
}
