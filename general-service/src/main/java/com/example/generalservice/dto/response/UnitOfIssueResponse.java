package com.example.generalservice.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.generalservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitOfIssueResponse {
    private Long id;
    private String uoiCode;
    private String uoiName;
    private Boolean uoiStatus;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;

    private String createdBy;
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;

}
