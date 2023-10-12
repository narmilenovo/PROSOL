package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseUOPResponse {
    private Long id;
    private String uopCode;
    private String uopName;
    private Boolean uopStatus;
}
