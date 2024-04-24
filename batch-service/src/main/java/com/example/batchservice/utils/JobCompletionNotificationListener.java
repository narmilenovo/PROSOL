package com.example.batchservice.utils;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

	@Override
	public void beforeJob(@NonNull JobExecution jobExecution) {
		// This method is called before the job starts.
	}

	@Override
	public void afterJob(@NonNull JobExecution jobExecution) {
		// This method is called after the job finishes.
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");
		}
	}
}
