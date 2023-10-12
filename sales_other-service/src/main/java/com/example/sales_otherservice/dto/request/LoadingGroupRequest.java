package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoadingGroupRequest {
    private String lgCode;
    private String lgName;
    private Boolean lgStatus;
}

