package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitOfIssueRequest {
    private String uoiCode;
    private String uoiName;
    private Boolean uoiStatus;

}
