package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseUOPRequest {
    private String uopCode;
    private String uopName;
    private Boolean uopStatus;
}
