package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionChannelResponse {
    private Long id;
    private String dcCode;
    private String dcName;
    private Boolean dcStatus;
    private SalesOrganizationResponse salesOrganization;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
