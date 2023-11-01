package com.example.generalsettings.response;

import lombok.Data;

@Data
public class SubMainGroupResponse {
	 	private Long id;
	 	private String subMainGroupCode;
	    private String subMainGroupTitle;
	    private Boolean status;
	    private Long mainGroupCodesId;
}
