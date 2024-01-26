package com.example.generalsettings.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainGroupCodesRequest {

    @Schema(description = "Main Group Code", example = "MGC-001")
    private String mainGroupCode;

    @Schema(description = "Main Group Name")
    private String mainGroupName;

    @Schema(description = "Main Group Status", example = "true")
    private Boolean mainGroupStatus;
}
