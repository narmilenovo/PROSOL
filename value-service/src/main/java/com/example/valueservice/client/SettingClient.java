package com.example.valueservice.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface SettingClient {

    @GetExchange("/getAllAttributeUom/{uomId}")
    AttributeUomResponse getUomById(@PathVariable Long uomId);

    @GetExchange("/getAttributeUomById/{id}")
    AttributeUomResponse getAttributeUomById(@PathVariable Long id);
}
