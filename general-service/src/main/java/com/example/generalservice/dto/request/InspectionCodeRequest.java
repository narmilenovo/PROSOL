package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionCodeRequest {
    private String inCodeCode;
    private String inCodeName;
    private Boolean inCodeStatus;
}
