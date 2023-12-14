package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionChannelRequest {
    @Schema(description = "Distribution Channel Code", example = "DC001")
    private String dcCode;

    @Schema(description = "Distribution Channel Name", example = "DC001")
    private String dcName;

    @Schema(description = "Distribution Channel Status", example = "true")
    private Boolean dcStatus;

    @Schema(description = "Sales Organization Id", example = "1")
    private Long salesOrganizationId;
}
