package com.example.mrpdataservice.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Helpers {
	private Helpers() {

	}

	public static void inputTitleCase(Object object, List<String> fieldsToSkipCapitalization) {
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				if (value instanceof String string && !fieldsToSkipCapitalization.contains(field.getName())) {
					String firstCaps = capitalize(string);
					field.set(object, firstCaps);
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		}
	}

	public static void inputTitleCase(Object object) {
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				if (value instanceof String string) {
					String firstCaps = capitalize(string);
					field.set(object, firstCaps);
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		}
	}

	public static String capitalize(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);

		return String.valueOf(chars);
	}

	public static String camelCaseWordsWithSpace(String str) {
		String[] words = str.split("\\s+");
		StringBuilder camelCase = new StringBuilder(words[0].toLowerCase());

		for (int i = 1; i < words.length; i++) {
			String word = words[i];
			if (!word.isEmpty()) {
				camelCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
			}
		}
		return camelCase.toString();
	}

	public static String capitalizeWordsWithSpace(String str) {
		str = splitCamelCase(str);
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (i == 0 || chars[i - 1] == ' ') {
				chars[i] = Character.toUpperCase(chars[i]);
			}
		}
		return String.valueOf(chars);
	}

	public static String splitCamelCase(String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char currentChar = str.charAt(i);
			if (i > 0) {
				char previousChar = str.charAt(i - 1);
				boolean isUpperCase = Character.isUpperCase(currentChar);
				boolean isDigit = Character.isDigit(currentChar);

				if ((isUpperCase && !Character.isUpperCase(previousChar))
						|| (isDigit && !Character.isDigit(previousChar))) {
					result.append(' ');
				}
			}
			result.append(currentChar);
		}
		return result.toString();
	}

	public static String toTitleCase(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		String[] words = input.split("\\s|-|_");
		StringBuilder titleCase = new StringBuilder();

		for (String word : words) {
			if (!word.isEmpty()) {
				titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase())
						.append(" ");
			}
		}

		return titleCase.toString().trim();
	}

	public static void updateErrorHashMap(Map<String, List<String>> errors, String field, String message) {
		List<String> strings;
		if (errors.containsKey(field)) {
			strings = errors.get(field);

		} else {
			strings = new ArrayList<>();

		}
		strings.add(message);
		errors.put(field, strings);

	}
}
