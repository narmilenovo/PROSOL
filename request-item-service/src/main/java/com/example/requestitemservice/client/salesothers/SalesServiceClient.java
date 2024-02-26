package com.example.requestitemservice.client.salesothers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sales-service", url = "http://localhost:8004")
public interface SalesServiceClient {

	@GetMapping("/getMsgById/{id}")
	public MaterialStrategicGroupResponse getMsgById(@PathVariable Long id);
}
