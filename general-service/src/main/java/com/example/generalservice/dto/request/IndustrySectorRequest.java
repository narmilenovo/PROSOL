package com.example.generalservice.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndustrySectorRequest {

    @Schema(description = "Sector Code", example = "SC-001")
    private String sectorCode;

    @Schema(description = "Sector Name")
    private String sectorName;

    @Schema(description = "Sector Status", example = "true")
    private Boolean sectorStatus;

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
