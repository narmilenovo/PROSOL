package com.example.attributemaster.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.attributemaster.entity.FieldType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AttributeMasterResponse {
    private Long id;
    private String attributeName;
    // @Enumerated(EnumType.STRING)
    private FieldType fieldType;
    private List<Long> listUom;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}