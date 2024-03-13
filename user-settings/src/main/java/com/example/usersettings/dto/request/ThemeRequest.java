package com.example.usersettings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThemeRequest {
	private String name;
	private String primaryColor;
	private String secondaryColor;
	private String tertiaryColor;
}
