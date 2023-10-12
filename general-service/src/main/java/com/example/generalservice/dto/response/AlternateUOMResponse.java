package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternateUOMResponse implements Serializable {
    private Long id;
    private String uomCode;
    private String uomName;
    private Boolean uomStatus;
}