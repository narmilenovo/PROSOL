package com.example.generalsettings.response;

import lombok.Data;

@Data
public class SubChildGroupResponse {
		private Long subId;
	 	private String code;
	    private String title;
	    private Boolean status;
	    private Long mainGroupCodesId;
	    private Long subMainGroupId;
}
