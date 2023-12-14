package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitOfIssueRequest {

    @Schema(description = "UOI Code", example = "UOI001")
    private String uoiCode;

    @Schema(description = "UOI Name", example = "Unit Of Issue")
    private String uoiName;

    @Schema(description = "UOI Status", example = "true")
    private Boolean uoiStatus;

}
