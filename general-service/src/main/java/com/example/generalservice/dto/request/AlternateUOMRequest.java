package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternateUOMRequest implements Serializable {
    private String uomCode;
    private String uomName;
    private Boolean uomStatus;
}