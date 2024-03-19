package com.example.batchservice.configuration;

import org.springframework.batch.item.ItemProcessor;

import com.example.batchservice.request.UserRequest;

public class UserRequestProcessor implements ItemProcessor<UserRequest, UserRequest> {

	@Override
	public UserRequest process(UserRequest userRequest) throws Exception {
		return userRequest;
	}

}
