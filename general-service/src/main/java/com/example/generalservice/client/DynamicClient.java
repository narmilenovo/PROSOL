package com.example.generalservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DYNAMIC-SERVICE")
public interface DynamicClient {

    @GetMapping("/checkFieldNameInForm")
    boolean checkFieldNameInForm(@RequestParam String fieldName, @RequestParam String formName);
}
