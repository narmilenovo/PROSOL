package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubChildGroupRequest {
	 private String code;
	    private String title;
	    private Boolean status;
	    private Long mainGroupCodesId;
	    private Long subMainGroupId;
}
