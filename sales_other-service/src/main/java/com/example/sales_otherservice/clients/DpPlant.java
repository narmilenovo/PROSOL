package com.example.sales_otherservice.clients;

import java.util.Date;
import java.util.Map;

import com.example.sales_otherservice.clients.Plant.PlantResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DpPlant {
    private Long id;
    private String dpCode;
    private String dpName;
    private Boolean dpStatus;
    private PlantResponse plant;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
