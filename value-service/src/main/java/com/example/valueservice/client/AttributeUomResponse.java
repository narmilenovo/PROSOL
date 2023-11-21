package com.example.valueservice.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a z")
    private Date createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a z")
    private Date updatedAt;
}
