package com.example.createtemplateservice.client.generalsettings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.createtemplateservice.client.AttributeUomResponse;

@FeignClient(name = "setting-service", url = "${clients.setting.url}")
public interface SettingServiceClient {

	@GetMapping("/getNmUomById/{id}")
	NmUomResponse getNmUomById(@PathVariable Long id);

	@GetMapping("/getAttributeUomById/{id}")
	AttributeUomResponse getAttributeUomById(@PathVariable Long id);
}
