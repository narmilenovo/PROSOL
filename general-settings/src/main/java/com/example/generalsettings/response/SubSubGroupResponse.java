package com.example.generalsettings.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubSubGroupResponse {
    private Long Id;
    private String subSubGroupCode;
    private String subSubGroupName;
    private Boolean subSubGroupStatus;
    private Long mainGroupCodesId;
    private Long subGroupCodesId;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
