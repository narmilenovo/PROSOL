package com.example.batchservice.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
//@Setter
//@Getter
public class BadRequestResponse {
    private Map<String, String> data;

    @JsonAnyGetter
    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
