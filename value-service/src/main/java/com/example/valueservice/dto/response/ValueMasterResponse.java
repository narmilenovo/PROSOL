package com.example.valueservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueMasterResponse {

    private Long id;
    private String value;
    private String abbreviation;
    private String abbreviationUnit;
    private String equivalent;
    private String equivalentUnit;
    private String likelyWords;
}
