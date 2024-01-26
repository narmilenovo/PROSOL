package com.example.plantservice.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuationClassRequest {

    @Schema(description = "Valuation Class Code", example = "VCL001")
    private String valuationClassCode;

    @Schema(description = "Valuation Class Name", example = "Valuation Class 1") // true = active, false = inactive
    private String valuationClassName;

    @Schema(description = "Select Material Type", example = "1") // true = active, false = inactive
    private Long materialTypeId;

    @Schema(description = "valuation class status", example = "true")
    private Boolean valuationClassStatus;

    @JsonIgnore
    private Map<String, Object> dynamicFields = new HashMap<>(); // Changed the value type to String

    @JsonAnyGetter
    public Map<String, Object> getDynamicFields() {
        return dynamicFields;
    }

    @JsonAnySetter
    public void setDynamicFields(String key, Object value) {
        this.dynamicFields.put(key, value);
    }
}
