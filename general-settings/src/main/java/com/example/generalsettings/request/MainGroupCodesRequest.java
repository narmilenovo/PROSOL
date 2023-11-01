package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainGroupCodesRequest {
	 private String mainGroupCode;
	    private String mainGroupName;
	    private Boolean mainGroupStatus;
}
