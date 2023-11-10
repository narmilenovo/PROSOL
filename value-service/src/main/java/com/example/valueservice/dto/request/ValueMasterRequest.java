package com.example.valueservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueMasterRequest {
    private String value;
    private String abbreviation;
    private String equivalent;
    private String likelyWords;
    private String abbreviationUnit;
    private String equivalentUnit;
}
