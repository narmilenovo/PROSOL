package com.example.generalsettings.response;

import java.util.Date;
import java.util.List;

import com.example.generalsettings.entity.UpdateAuditHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTypeResponse {
    private Long id;
    private String referenceTypeCode;
    private String referenceTypeName;
    private Boolean referenceTypeStatus;
    private Boolean duplicateCheck;

    private String createdBy;
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;
}