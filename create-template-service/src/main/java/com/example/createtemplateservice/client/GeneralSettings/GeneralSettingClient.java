package com.example.createtemplateservice.client.GeneralSettings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("SETTING-SERVICE")
public interface GeneralSettingClient {
    @GetMapping("/getNmUomById/{id}")
    NmUom getNmUomById(@PathVariable Long id);

    @GetMapping("/getAttributeUomById/{id}")
    AttributeUom getAttributeUomById(@PathVariable Long id);
}
