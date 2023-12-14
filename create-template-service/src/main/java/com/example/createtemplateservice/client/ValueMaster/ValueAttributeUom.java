package com.example.createtemplateservice.client.ValueMaster;

import lombok.Data;

import java.util.Date;

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
}
