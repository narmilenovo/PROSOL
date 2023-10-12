package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ItemCategoryGroupRequest {
    private String icgCode;
    private String icgName;
    private Boolean icgStatus;
}

