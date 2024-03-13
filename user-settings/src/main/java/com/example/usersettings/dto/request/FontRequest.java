package com.example.usersettings.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontRequest {

	private String fontName;

	private List<FontPropertyRequest> fontProperties;

	private ThemeRequest theme;
}
