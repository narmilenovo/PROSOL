package com.example.generalsettings.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceTypeResponse {
    private Long id;
    private String sourceTypeCode;
    private String sourceTypeName;
    private Boolean sourceTypeStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
