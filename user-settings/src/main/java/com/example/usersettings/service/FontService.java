package com.example.usersettings.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.usersettings.configuration.ResourceNotFoundException;
import com.example.usersettings.dto.request.FontPropertyRequest;
import com.example.usersettings.dto.request.FontRequest;
import com.example.usersettings.dto.request.ThemeRequest;
import com.example.usersettings.dto.response.FontResponse;
import com.example.usersettings.entity.Font;
import com.example.usersettings.entity.FontProperty;
import com.example.usersettings.entity.Theme;
import com.example.usersettings.mapping.FontMapper;
import com.example.usersettings.repository.FontRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FontService {

	private final FontRepository fontRepository;
	private final FontMapper fontMapper;
	private final HttpServletRequest request;

	public FontResponse updateFont(FontRequest fontRequest) {
		String userId = request.getHeader("X-User-Id");
		Optional<Font> exist = fontRepository.findByCreatedBy(userId);
		if (exist.isPresent()) {
			Font existingFont = exist.get();
			updateExistingFont(existingFont, fontRequest);
			Font updatedFont = fontRepository.save(existingFont);
			return fontMapper.mapToFontResponse(updatedFont);
		} else {
			Font newFont = createNewFont(fontRequest);
			Font saveFont = fontRepository.save(newFont);
			return fontMapper.mapToFontResponse(saveFont);
		}
	}

	private void updateExistingFont(Font existingFont, FontRequest fontRequest) {
		// Update fontName
		existingFont.setId(existingFont.getId());
		existingFont.setFontName(fontRequest.getFontName());

		// Update fontProperties
		List<FontProperty> existingProperties = existingFont.getFontProperties();
		List<FontPropertyRequest> propertyRequests = fontRequest.getFontProperties();

		for (int i = 0; i < Math.min(existingProperties.size(), propertyRequests.size()); i++) {
			FontProperty existingProperty = existingProperties.get(i);
			FontPropertyRequest propertyRequest = propertyRequests.get(i);

			existingProperty.setFontFormat(propertyRequest.getFontFormat());
			existingProperty.setFontSize(propertyRequest.getFontSize());
			existingProperty.setFontWeight(propertyRequest.getFontWeight());
		}

		// Update theme
		Theme existingTheme = existingFont.getTheme();
		ThemeRequest themeRequest = fontRequest.getTheme();

		if (existingTheme != null && themeRequest != null) {
			existingTheme.setName(themeRequest.getName());
			existingTheme.setPrimaryColor(themeRequest.getPrimaryColor());
			existingTheme.setSecondaryColor(themeRequest.getSecondaryColor());
			existingTheme.setTertiaryColor(themeRequest.getTertiaryColor());
		}

	}

	private Font createNewFont(FontRequest fontRequest) {
		Font newFont = new Font();
		newFont.setFontName(fontRequest.getFontName());

		List<FontProperty> fontProperties = new ArrayList<>();
		for (FontPropertyRequest propertyRequest : fontRequest.getFontProperties()) {
			FontProperty fontProperty = new FontProperty();
			fontProperty.setFontFormat(propertyRequest.getFontFormat());
			fontProperty.setFontSize(propertyRequest.getFontSize());
			fontProperty.setFontWeight(propertyRequest.getFontWeight());

			fontProperties.add(fontProperty);
		}
		newFont.setFontProperties(fontProperties);

		if (fontRequest.getTheme() != null) {
			Theme theme = new Theme();
			theme.setName(fontRequest.getTheme().getName());
			theme.setPrimaryColor(fontRequest.getTheme().getPrimaryColor());
			theme.setSecondaryColor(fontRequest.getTheme().getSecondaryColor());
			theme.setTertiaryColor(fontRequest.getTheme().getTertiaryColor());
			newFont.setTheme(theme);
		}
		return newFont;
	}

	public FontResponse getFont() {
		String userId = request.getHeader("X-User-Id");
		Optional<Font> exist = fontRepository.findByCreatedBy(userId);
		if (exist.isPresent()) {
			return fontMapper.mapToFontResponse(exist.get());
		} else {
			throw new ResourceNotFoundException("Font not found for this userName: " + userId);
		}
	}
}
