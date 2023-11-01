package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionTypeResponse {
    private Long id;
    private String inTypeCode;
    private String inTypeName;
    private Boolean inTypeStatus;

}
