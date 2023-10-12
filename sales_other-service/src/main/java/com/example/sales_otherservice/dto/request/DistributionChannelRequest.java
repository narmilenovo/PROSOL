package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionChannelRequest {
    private String dcCode;
    private String dcName;
    private Boolean dcStatus;
    private String salesOrganizationName;
}
