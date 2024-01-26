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
public class StorageBinRequest {

    @Schema(description = "Code of the storage bin", example = "BIN001")
    private String storageBinCode;

    @Schema(description = "Name of the storage bin", example = "Storage Bin 1")
    private String storageBinName;

    @Schema(description = "Status of the storage bin", example = "true")
    private Boolean storageBinStatus;

    @Schema(description = "Select Plant", example = "1")
    private Long plantId;

    @Schema(description = "Select Storage location", example = "1")
    private Long storageLocationId;

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