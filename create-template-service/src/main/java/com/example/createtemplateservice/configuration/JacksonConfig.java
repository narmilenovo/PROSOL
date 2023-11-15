package com.example.createtemplateservice.configuration;

import com.example.createtemplateservice.utils.LocalDateTimeDeserializer;
import com.example.createtemplateservice.utils.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(Date.class, new LocalDateTimeSerializer())
                .addDeserializer(Date.class, new LocalDateTimeDeserializer())
        );
        return objectMapper;
    }
}