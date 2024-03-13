package com.example.usersettings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontPropertyResponse {
	private Long id;

	private String fontFormat;

	private Integer fontSize;

	private String fontWeight;
}
