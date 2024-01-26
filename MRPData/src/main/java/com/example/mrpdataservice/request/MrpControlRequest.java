package com.example.mrpdataservice.request;

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
public class MrpControlRequest {

    @Schema(description = "Mrp Control Code", example = "MRP001")
    private String mrpControlCode;

    @Schema(description = "Mrp Control Name")
    private String mrpControlName;

    @Schema(description = "Mrp Control Status", example = "true")
    private Boolean mrpControlStatus;

    @Schema(description = "Select Plant", example = "1")
    private Long plantId;

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
