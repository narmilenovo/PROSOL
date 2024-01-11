package com.example.sales_otherservice.dto.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderUnitResponse {
    private Long id;
    private String ouCode;
    private String ouName;
    private Boolean ouStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
