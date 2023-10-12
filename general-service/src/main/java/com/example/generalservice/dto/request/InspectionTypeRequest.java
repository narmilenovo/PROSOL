package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionTypeRequest {
    private String inTypeCode;
    private String inTypeName;
    private Boolean inTypeStatus;

}
