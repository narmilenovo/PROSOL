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
public class SourceTypeResponse {
    private Long id;
    private String sourceTypeCode;
    private String sourceTypeName;
    private Boolean sourceTypeStatus;

    private String createdBy;
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;
}
