package com.example.generalsettings.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeUomRequest {
	 private String attributeUomCode;
	    private String attributeUomName;
	    private Boolean attributeUomStatus;
}
