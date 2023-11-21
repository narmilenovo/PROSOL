package com.example.valueservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "SETTING-SERVICE", url = "http://localhost:8006", configuration = ClientConfig.class)
public interface SettingsClient {

    @GetMapping("/getAttributeUomById/{id}")
    ResponseEntity<Object> getAttributeUomById(@PathVariable Long id);

    @GetMapping(value = "/getAllAttributeUom/{uomId}", produces = "application/json", consumes = "application/json")
    AttributeUomResponse getUomById(@PathVariable Long uomId);
}

