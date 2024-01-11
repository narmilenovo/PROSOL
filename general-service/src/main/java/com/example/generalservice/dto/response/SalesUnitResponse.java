package com.example.generalservice.dto.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesUnitResponse {
    private Long id;
    private String salesCode;
    private String salesName;
    private Boolean salesStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
