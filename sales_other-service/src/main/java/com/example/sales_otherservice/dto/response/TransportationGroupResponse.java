package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransportationGroupResponse {
    private Long id;
    private String tgCode;
    private String tgName;
    private Boolean tgStatus;
}

