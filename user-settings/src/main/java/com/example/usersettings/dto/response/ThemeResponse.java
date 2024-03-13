package com.example.usersettings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThemeResponse {

	private Long id;
	private String name;
	private String primaryColor;
	private String secondaryColor;
	private String tertiaryColor;

}
