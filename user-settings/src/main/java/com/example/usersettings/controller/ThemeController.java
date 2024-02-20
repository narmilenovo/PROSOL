package com.example.usersettings.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.usersettings.dto.ThemeRequest;
import com.example.usersettings.dto.ThemeResponse;
import com.example.usersettings.service.ThemeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ThemeController {
	private final ThemeService themeService;

	@PutMapping("/updateTheme")
	public ThemeResponse updateTheme(@RequestBody ThemeRequest themeRequest) {
		return themeService.updateTheme(themeRequest);
	}

	@GetMapping("/getTheme")
	public ThemeResponse getTheme() {
		return themeService.getTheme();
	}

}
