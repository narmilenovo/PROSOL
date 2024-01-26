package com.example.valueservice.client;

import java.util.Date;

import com.example.valueservice.client.GeneralSetting.AttributeUom;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueAttributeUom {
    private Long id;
    private String value;
    private String abbreviation;
    private AttributeUom abbreviationUnit;
    private String equivalent;
    private AttributeUom equivalentUnit;
    private String likelyWords;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
