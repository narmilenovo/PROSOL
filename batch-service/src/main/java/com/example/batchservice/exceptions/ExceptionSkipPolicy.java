package com.example.batchservice.exceptions;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class ExceptionSkipPolicy implements SkipPolicy {

	@Override
	public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
		return throwable instanceof NumberFormatException;
	}

}
