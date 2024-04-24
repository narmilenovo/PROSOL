package com.example.batchservice.user;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchservice.exceptions.ExceptionSkipPolicy;
import com.example.batchservice.utils.StepSkipListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final UserItemWriter userItemWriter;

	@Bean
	@StepScope
	FlatFileItemReader<UserRequest> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle) {
		FlatFileItemReader<UserRequest> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(new File(pathToFIle)));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	private LineMapper<UserRequest> lineMapper() {

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("email", "password", "confirmPassword", "firstName", "lastName", "phone", "business",
				"departmentId", "plantId", "status", "roles");

		DefaultLineMapper<UserRequest> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(new UserRequestFieldSetMapper());

		return lineMapper;
	}

	@Bean
	UserRequestProcessor processor() {
		return new UserRequestProcessor();
	}

	@Bean
	SkipPolicy skipPolicy() {
		return new ExceptionSkipPolicy();
	}

	@Bean
	SkipListener<UserRequest, Number> skipListener() {
		return new StepSkipListener();
	}

	@Bean
	TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(100);
		return taskExecutor;
	}

	@Bean
	Step step1(@NonNull FlatFileItemReader<UserRequest> itemReader) {
		return new StepBuilder("slaveStep", jobRepository)
				.<UserRequest, UserRequest>chunk(1000, platformTransactionManager).reader(itemReader)
				.processor(processor()).writer(userItemWriter).faultTolerant().listener(skipListener())
				.skipPolicy(skipPolicy()).taskExecutor(taskExecutor()).build();
	}

	@Bean
	Job runJob(@NonNull FlatFileItemReader<UserRequest> itemReader) {
		return new JobBuilder("importUsers", jobRepository).flow(step1(itemReader)).end().build();

	}

}
