package com.example.attributemaster.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.attributemaster.client.GeneralSettings.AttributeUomResponse;
import com.example.attributemaster.entity.FieldType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttributeMasterUomResponse {
    private Long id;
    private String attributeName;
    // @Enumerated(EnumType.STRING)
    private FieldType fieldType;
    private List<AttributeUomResponse> listUom;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
