package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ItemCategoryGroupResponse {
    private Long id;
    private String icgCode;
    private String icgName;
    private Boolean icgStatus;
}

