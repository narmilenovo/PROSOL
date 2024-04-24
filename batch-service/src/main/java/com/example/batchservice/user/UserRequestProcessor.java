package com.example.batchservice.user;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class UserRequestProcessor implements ItemProcessor<UserRequest, UserRequest> {

	@Override
	public UserRequest process(@NonNull UserRequest userRequest) throws Exception {
		return userRequest;
	}

}
