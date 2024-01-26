package com.example.valueservice.client.GeneralSetting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SETTING-SERVICE")
public interface SettingClient {

    @GetMapping("/getAllAttributeUom/{uomId}")
    AttributeUom getUomById(@PathVariable Long uomId);

    @GetMapping("/getAttributeUomById/{id}")
    AttributeUom getAttributeUomById(@PathVariable Long id);
}
