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
public class TransportationGroupResponse {
    private Long id;
    private String tgCode;
    private String tgName;
    private Boolean tgStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
