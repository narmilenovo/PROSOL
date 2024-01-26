package com.example.generalsettings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubSubGroupRequest {

    @Schema(description = "Sub Sub Code")
    private String subSubGroupCode;

    @Schema(description = "Sub Sub Name")
    private String subSubGroupName;

    @Schema(description = "Sub Sub Status", example = "true")
    private Boolean subSubGroupStatus;

    @Schema(description = "Select Main Group Codes")
    private Long mainGroupCodesId;

    @Schema(description = " Select Sub Group", example = "1", allowableValues = "range[1, infinity]")
    private Long subGroupId;
}
