package com.example.usersettings.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.usersettings.configuration.ResourceNotFoundException;
import com.example.usersettings.dto.ThemeRequest;
import com.example.usersettings.dto.ThemeResponse;
import com.example.usersettings.entity.Theme;
import com.example.usersettings.repository.ThemeRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {
	private final ThemeRepository themeRepository;
	private final ModelMapper modelMapper;
	private final HttpServletRequest request;

	public ThemeResponse updateTheme(ThemeRequest themeRequest) {
		String userId = request.getHeader("X-User-Id");
		Optional<Theme> exist = themeRepository.findByCreatedBy(userId);
		if (exist.isPresent()) {
			Theme existingTheme = exist.get();
			updateExistingTheme(existingTheme, themeRequest);
			Theme updated = themeRepository.save(existingTheme);
			return mapToThemeResponse(updated);
		} else {
			Theme newTheme = createNewTheme(themeRequest);
			Theme saveTheme = themeRepository.save(newTheme);
			return mapToThemeResponse(saveTheme);
		}
	}

	private void updateExistingTheme(Theme existingTheme, ThemeRequest themeRequest) {
		existingTheme.setName(themeRequest.getName());
		existingTheme.setPrimaryColor(themeRequest.getPrimaryColor());
		existingTheme.setSecondaryColor(themeRequest.getSecondaryColor());
		existingTheme.setTertiaryColor(themeRequest.getTertiaryColor());

	}

	private Theme createNewTheme(ThemeRequest themeRequest) {
		Theme newTheme = new Theme();
		newTheme.setName(themeRequest.getName());
		newTheme.setPrimaryColor(themeRequest.getPrimaryColor());
		newTheme.setSecondaryColor(themeRequest.getSecondaryColor());
		newTheme.setTertiaryColor(themeRequest.getTertiaryColor());
		return newTheme;
	}

	public ThemeResponse getTheme() {
		String userId = request.getHeader("X-User-Id");
		Optional<Theme> exist = themeRepository.findByCreatedBy(userId);
		if (exist.isPresent()) {
			return mapToThemeResponse(exist.get());
		} else {
			throw new ResourceNotFoundException("Theme not found for this userName: " + userId);
		}
	}

	private ThemeResponse mapToThemeResponse(Theme theme) {
		return modelMapper.map(theme, ThemeResponse.class);
	}
}
