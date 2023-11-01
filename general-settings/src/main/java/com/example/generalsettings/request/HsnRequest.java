package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HsnRequest {
		private String hsnCode;
	    private String hsnDesc;
	    private Boolean hsnStatus;
}
