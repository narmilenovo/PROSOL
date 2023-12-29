package com.example.requestitemservice.client.general;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GENERAL-SERVICE")
public interface GeneralServiceClient {
    @GetMapping("/getMaterialById/{id}")
    public MaterialTypeResponse getMaterialById(@PathVariable Long id);

    @GetMapping("/getSectorById/{id}")
    public IndustrySectorResponse getSectorById(@PathVariable Long id);
}
