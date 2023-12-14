package com.example.createtemplateservice.jpa.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryAttributeRequest {
    private Long attributeId;
    private Integer shortPriority;
    private Boolean mandatory;
    private String definition;
    private List<Long> valueId;
    private Boolean uomMandatory;
    private List<Long> attrUomId;
}
