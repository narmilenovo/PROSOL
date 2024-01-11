package com.example.sales_otherservice.dto.request;

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
public class AccAssignmentRequest {

    @Schema(description = "Account Assignment Code", example = "ACC001")
    private String accCode;

    @Schema(description = "Account Assignment Name", example = "Account Assignment Name")
    private String accName;

    @Schema(description = "Account Assignment Status", example = "true")
    private Boolean accStatus;

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
