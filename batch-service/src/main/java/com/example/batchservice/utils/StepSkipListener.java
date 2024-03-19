package com.example.batchservice.utils;

import org.springframework.batch.core.SkipListener;

import com.example.batchservice.request.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StepSkipListener implements SkipListener<UserRequest, Number> {

	@Override // item reader
	public void onSkipInRead(Throwable throwable) {
		log.info("A failure on read {} ", throwable.getMessage());
	}

	@Override // item writter
	public void onSkipInWrite(Number item, Throwable throwable) {
		log.info("A failure on write {} , {}", throwable.getMessage(), item);
	}

	@SneakyThrows
	@Override // item processor
	public void onSkipInProcess(UserRequest customer, Throwable throwable) {
		log.info("Item {}  was skipped due to the exception  {}", new ObjectMapper().writeValueAsString(customer),
				throwable.getMessage());
	}
}
