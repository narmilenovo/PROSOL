package com.example.valueservice.dto.response;

import com.example.valueservice.client.AttributeUomResponse;
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
    private AttributeUomResponse abbreviationUnit;
    private String equivalent;
    private AttributeUomResponse equivalentUnit;
    private String likelyWords;
}
