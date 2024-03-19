package com.example.createtemplateservice.utils;

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

	public static String titleCaseWithSpace(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		String[] words = input.split("[\\s-_]");
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
