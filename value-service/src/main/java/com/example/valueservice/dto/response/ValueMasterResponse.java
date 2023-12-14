package com.example.valueservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueMasterResponse {

    private Long id;
    private String value;
    private String abbreviation;
    private Long abbreviationUnit;
    private String equivalent;
    private Long equivalentUnit;
    private String likelyWords;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
