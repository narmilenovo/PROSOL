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
    private Long abbreviationUnit;
    private String equivalent;
    private Long equivalentUnit;
    private String likelyWords;
}
