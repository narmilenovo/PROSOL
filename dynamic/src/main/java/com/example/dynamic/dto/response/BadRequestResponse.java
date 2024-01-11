package com.example.dynamic.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
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
