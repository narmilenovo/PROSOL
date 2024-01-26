package com.example.generalsettings.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTypeResponse {
    private Long id;
    private String referenceTypeCode;
    private String referenceTypeName;
    private Boolean referenceTypeStatus;
    private Boolean duplicateCheck;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
