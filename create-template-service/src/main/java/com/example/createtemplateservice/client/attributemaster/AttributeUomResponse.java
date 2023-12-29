package com.example.createtemplateservice.client.attributemaster;


import lombok.Data;


@Data
public class AttributeUomResponse {
    private Long id;
    private String attributeUomCode;
    private String attributeUomName;
    private Boolean attributeUomStatus;
    private String createdBy;
    private String updatedBy;


}
