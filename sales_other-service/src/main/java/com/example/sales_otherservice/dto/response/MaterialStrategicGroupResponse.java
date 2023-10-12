package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialStrategicGroupResponse {
    private Long id;
    private String msCode;
    private String msName;
    private Boolean msStatus;
}
