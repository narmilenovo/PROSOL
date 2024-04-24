package com.example.batchservice.user;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "http://localhost:8000")
public interface UserServiceClient {

	@PostMapping("/saveAllUser")
	public void bulkSave(@RequestBody List<UserRequest> userRequests);
}
