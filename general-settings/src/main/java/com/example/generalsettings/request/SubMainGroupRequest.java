package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubMainGroupRequest {
	    private String subMainGroupCode;
	    private String subMainGroupTitle;
	    private Boolean status;
	    private Long mainGroupCodesId;
}
