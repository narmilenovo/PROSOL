package com.example.attributemaster.client.GeneralSettings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "setting-service", url = "${clients.setting.url}")
public interface SettingServiceClient {

	@GetMapping("/getAttributeUomById/{id}")
	AttributeUomResponse getAttributeUomById(@PathVariable Long id);
}
