package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoadingGroupResponse {
    private Long id;
    private String lgCode;
    private String lgName;
    private Boolean lgStatus;
}

