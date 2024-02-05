package com.example.createtemplateservice.client.attributemaster;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "attribute-service", url = "http://localhost:8010")
public interface AttributeClient {
	@GetMapping("/getAttributeMasterById/{id}")
	AttributeMasterUomResponse getAttributeMasterById(@PathVariable Long id);
}
