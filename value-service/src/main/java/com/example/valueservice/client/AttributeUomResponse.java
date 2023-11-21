package com.example.valueservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeUomResponse {
    private Long id;
    private String attributeUomCode;
    private String attributeUomName;
    private Boolean attributeUomStatus;
    private String createdBy;
    private String updatedBy;
//    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a z")
//    private Date createdAt;
//    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a z")
//    private Date updatedAt;
}
