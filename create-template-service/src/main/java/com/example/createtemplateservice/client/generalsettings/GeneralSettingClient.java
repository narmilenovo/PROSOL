package com.example.createtemplateservice.client.generalsettings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "setting-service", url = "http://localhost:8006")
public interface GeneralSettingClient {
	@GetMapping("/getNmUomById/{id}")
	NmUom getNmUomById(@PathVariable Long id);

	@GetMapping("/getAttributeUomById/{id}")
	AttributeUom getAttributeUomById(@PathVariable Long id);
}
