package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransportationGroupRequest {
    private String tgCode;
    private String tgName;
    private Boolean tgStatus;
}

