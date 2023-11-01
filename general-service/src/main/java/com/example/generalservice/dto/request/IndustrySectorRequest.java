package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndustrySectorRequest {
    private String sectorCode;
    private String sectorName;
    private Boolean sectorStatus;
}
