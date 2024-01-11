package com.example.dynamic.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropDownRequest {
    private String value;

    @JsonIgnore
    private FieldRequest field;
}
