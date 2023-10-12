package com.example.sales_otherservice.dto.response;

import com.example.sales_otherservice.entity.SalesOrganization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionChannelResponse {
    private Long id;
    private String dcCode;
    private String dcName;
    private Boolean dcStatus;
    private SalesOrganization salesOrganization;
}
