package com.example.batchservice.configuration;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.batchservice.utils.LocalDateTimeDeserializer;
import com.example.batchservice.utils.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new SimpleModule().addSerializer(Date.class, new LocalDateTimeSerializer())
				.addDeserializer(Date.class, new LocalDateTimeDeserializer()));
		return objectMapper;
	}
}
