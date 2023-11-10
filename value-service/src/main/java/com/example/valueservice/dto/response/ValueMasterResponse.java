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
    private String equivalent;
    private String likelyWords;
    private String abbreviationUnit;
    private String equivalentUnit;
}
