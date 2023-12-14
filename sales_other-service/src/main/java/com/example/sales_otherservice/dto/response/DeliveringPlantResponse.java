package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveringPlantResponse {
    private Long id;
    private String dpCode;
    private String dpName;
    private Boolean dpStatus;
    private Long plantId;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
