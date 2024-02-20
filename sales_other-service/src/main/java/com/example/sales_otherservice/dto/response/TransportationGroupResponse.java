package com.example.sales_otherservice.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.sales_otherservice.entity.UpdateAuditHistory;
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

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;

    private String createdBy;
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;
}
