package com.example.generalsettings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubGroupCodesRequest {

    @Schema(description = "Sub Group Code")
    private String subGroupCode;

    @Schema(description = "Sub Group Name")
    private String subGroupName;

    @Schema(description = "Sub Group Status", example = "true", allowableValues = "true, false")
    private Boolean subGroupStatus;

    @Schema(description = "Select Main Group Codes", example = "1", allowableValues = "range[1, infinity]")
    private Long mainGroupCodesId;
}
