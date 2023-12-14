package com.example.createtemplateservice.client;

import com.example.createtemplateservice.client.AttributeMaster.AttributeMasterUomResponse;
import com.example.createtemplateservice.client.GeneralSettings.AttributeUom;
import com.example.createtemplateservice.client.ValueMaster.ValueAttributeUom;
import lombok.Data;

import java.util.List;

@Data
public class DictionaryAttributeAllResponse {
    private Long id;
    private AttributeMasterUomResponse attribute;
    private Integer shortPriority;
    private Boolean mandatory;
    private String definition;
    private List<ValueAttributeUom> values;
    private Boolean uomMandatory;
    private List<AttributeUom> attrUoms;
}
