package com.example.valueservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueMasterRequest {

    @Schema(description = "value", example = "value")
    @Size(min = 2, max = 30, message = "value must be between 2 and 30 characters long")
    @NotBlank(message = "value is required")
    private String value;

    @Schema(description = "abbreviation", example = "abbreviation")
    @NotBlank(message = "abbreviation is required")
    private String abbreviation;

    @Schema(description = "Select abbreviation Unit from one of the following")
    private Long abbreviationUnit;

    @Schema(description = "equivalent", example = "equivalent")
    private String equivalent;

    @Schema(description = "Select equivalent Unit from one of the following")
    private Long equivalentUnit;

    @Schema(description = "likely words", example = "likely words")
    private String likelyWords;
}
