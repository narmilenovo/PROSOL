package com.example.createtemplateservice.client.generalsettings;

import lombok.Data;

import java.util.Date;

@Data
public class AttributeUom {
    private Long id;
    private String attributeUomCode;
    private String attributeUomName;
    private Boolean attributeUomStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
