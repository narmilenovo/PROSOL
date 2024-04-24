package com.example.createtemplateservice.client.valuemaster;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "value-service", url = "${clients.value.url}")
public interface ValueServiceClient {

	@GetMapping("/getValueById/{id}")
	ValueAttributeUom getValueById(@PathVariable Long id, @RequestParam(required = false) Boolean attributeUom);
}
