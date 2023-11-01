package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionCodeResponse {
    private Long id;
    private String inCodeCode;
    private String inCodeName;
    private Boolean inCodeStatus;
}
