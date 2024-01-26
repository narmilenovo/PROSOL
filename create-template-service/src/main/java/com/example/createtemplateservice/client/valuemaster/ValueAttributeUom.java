package com.example.createtemplateservice.client.valuemaster;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
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

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
