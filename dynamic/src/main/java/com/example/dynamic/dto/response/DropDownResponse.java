package com.example.dynamic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropDownResponse {
    private String value;

    // @JsonIgnore
    // private FieldResponse field;
}
