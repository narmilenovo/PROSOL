package com.example.createtemplateservice.jpa.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryAttributeResponse {
    private Long id;
    private Long attributeId;
    private Integer shortPriority;
    private Boolean mandatory;
    private String definition;
    private List<Long> valueId;
    private Boolean uomMandatory;
    private List<Long> attrUomId;
}
