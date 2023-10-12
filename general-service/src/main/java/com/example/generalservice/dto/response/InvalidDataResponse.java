package com.example.generalservice.dto.response;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

//@Getter
//@Setter
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