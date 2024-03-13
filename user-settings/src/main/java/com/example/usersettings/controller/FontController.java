package com.example.usersettings.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.usersettings.dto.request.FontRequest;
import com.example.usersettings.dto.response.FontResponse;
import com.example.usersettings.service.FontService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FontController {

	private final FontService fontService;

	@PutMapping("/updateFont")
	public FontResponse updateFont(@RequestBody FontRequest fontRequest) {
		return fontService.updateFont(fontRequest);
	}

	@GetMapping("/getFont")
	public FontResponse getFont() {
		return fontService.getFont();
	}
}
