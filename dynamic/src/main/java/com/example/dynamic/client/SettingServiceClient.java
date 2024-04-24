package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "setting-service", url = "${clients.setting.url}")
public interface SettingServiceClient {

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getGeneralSettingsExistingFields(@PathVariable String formName)
			throws ClassNotFoundException;

	@GetMapping("/getSalesListOfFieldNameValues")
	public List<Object> getSalesListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);
}
