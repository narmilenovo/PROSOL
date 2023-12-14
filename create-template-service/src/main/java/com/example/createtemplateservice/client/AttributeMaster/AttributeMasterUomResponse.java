package com.example.createtemplateservice.client.AttributeMaster;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttributeMasterUomResponse {
    private Long id;
    private String attributeName;
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;
    private List<AttributeUomResponse> listUom;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;


}
