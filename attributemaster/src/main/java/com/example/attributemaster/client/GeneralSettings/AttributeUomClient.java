package com.example.attributemaster.client.GeneralSettings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SETTING-SERVICE")
public interface AttributeUomClient {

    @GetMapping("/getAttributeUomById/{id}")
    AttributeUomResponse getAttributeUomById(@PathVariable Long id);
}
