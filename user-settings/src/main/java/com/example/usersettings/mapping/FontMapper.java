package com.example.usersettings.mapping;

import org.mapstruct.Mapper;

import com.example.usersettings.dto.response.FontResponse;
import com.example.usersettings.entity.Font;

@Mapper(componentModel = "spring")
public interface FontMapper {

	FontResponse mapToFontResponse(Font font);
}
