package com.example.dynamic.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldRequest {

    private String fieldName;
    private String dataType;
    private String identity;

    private List<String> pattern;

    private Integer min;
    private Integer max;
    private Integer minLength;
    private Integer maxLength;

    private Boolean required = false;
    private Boolean extraField = true;
    private Boolean readable = true;
    private Boolean writable = true;
    private Boolean showAsColumn = true;

    private List<DropDownRequest> dropDowns;

    private List<String> enums;

    @JsonIgnore
    private FormRequest form;

}
