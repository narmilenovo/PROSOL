package com.example.sales_otherservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PLANT-SERVICE")
public interface PlantClient {

    @GetMapping("/getPlantById/{id}")
    PlantResponse getPlantById(@PathVariable Long id);

}