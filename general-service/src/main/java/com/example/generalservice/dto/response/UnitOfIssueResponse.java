package com.example.generalservice.dto.response;

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

}
