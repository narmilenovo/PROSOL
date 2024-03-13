package com.example.usersettings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontPropertyRequest {

	private String fontFormat;

	private Integer fontSize;

	private String fontWeight;
}
