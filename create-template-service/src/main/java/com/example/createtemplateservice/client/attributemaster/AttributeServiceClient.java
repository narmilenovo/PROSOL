package com.example.createtemplateservice.client.attributemaster;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "attribute-service", url = "${clients.attribute.url}")
public interface AttributeServiceClient {

	@GetMapping("/getAttributeMasterById/{id}")
	AttributeMasterUomResponse getAttributeMasterById(@PathVariable Long id);
}
