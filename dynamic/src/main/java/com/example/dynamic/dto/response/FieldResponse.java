package com.example.dynamic.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldResponse {

    private Long id;
    private String fieldName;
    private String dataType;
    private String identity;

    private List<String> pattern;

    private Integer min;
    private Integer max;
    private Integer minLength;
    private Integer maxLength;

    private Boolean required;
    private Boolean extraField;
    private Boolean readable;
    private Boolean writable;
    private Boolean showAsColumn;

    private List<DropDownResponse> dropDowns;

    private List<String> enums;

    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

    // @JsonIgnore
    // private FormResponse form;
}
