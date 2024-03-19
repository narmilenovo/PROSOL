package com.example.batchservice.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.batchservice.client.user.UserServiceClient;
import com.example.batchservice.request.UserRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserItemWriter implements ItemWriter<UserRequest> {

	private final UserServiceClient userServiceClient;

	@Override
	public void write(Chunk<? extends UserRequest> chunk) throws Exception {
//		System.out.println("Writer Thread " + Thread.currentThread().getName());

		List<UserRequest> userRequests = new ArrayList<>();
		for (UserRequest userRequest : chunk.getItems()) {
			userRequests.add(userRequest);
		}

		userServiceClient.bulkSave(userRequests);
	}

}
