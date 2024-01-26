package com.example.generalsettings.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainGroupCodesResponse {
    private Long id;
    private String mainGroupCode;
    private String mainGroupName;
    private Boolean mainGroupStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
