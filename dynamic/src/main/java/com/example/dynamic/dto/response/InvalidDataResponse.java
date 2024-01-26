package com.example.dynamic.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvalidDataResponse {
    private Map<String, Map<String, List<String>>> data;

    @JsonAnyGetter
    public Map<String, Map<String, List<String>>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, List<String>>> data) {
        this.data = data;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public InvalidDataResponse(@JsonProperty("data") Map<String, Map<String, List<String>>> data) {
        this.data = data;
    }
}