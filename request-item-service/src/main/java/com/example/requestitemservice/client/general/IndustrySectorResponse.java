package com.example.requestitemservice.client.general;

import java.util.Date;

import lombok.Data;

@Data
public class IndustrySectorResponse {
    private Long id;
    private String sectorCode;
    private String sectorName;
    private Boolean sectorStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
