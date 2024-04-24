package com.example.batchservice.plant;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.streaming.StreamingXlsxItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.batchservice.request.PlantRequest;

@Configuration
public class PlantBatchConfiguration {

	@Bean
	@StepScope
	StreamingXlsxItemReader<PlantRequest> excelReader() {
		StreamingXlsxItemReader<PlantRequest> reader = new StreamingXlsxItemReader<>();
		reader.setResource(new FileSystemResource("/path/to/your/excel/file"));
		reader.setRowMapper(rowMapper());
		return reader;
	}

	@Bean
	RowMapper<PlantRequest> rowMapper() {
		return new PlantRowMapper();
	}
}
