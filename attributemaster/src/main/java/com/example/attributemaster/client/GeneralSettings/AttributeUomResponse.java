package com.example.attributemaster.client.GeneralSettings;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeUomResponse {
    private Long id;
    private String attributeUomName;
    private String attributeUnit;
    private Boolean attributeUomStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

}
