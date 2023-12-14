package com.example.createtemplateservice.client.AttributeMaster;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ATTRIBUTE-SERVICE")
public interface AttributeClient {
    @GetMapping("/getAttributeMasterById/{id}")
    AttributeMasterUomResponse getAttributeMasterById(@PathVariable Long id);
}
