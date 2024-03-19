package com.example.dynamic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Helpers {

	private Helpers() {

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

	public static String toTitleCaseWithoutSpace(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		String firstChar = input.charAt(0) + "";
		String remainingChars = input.substring(1);

		return firstChar.toUpperCase() + remainingChars.toLowerCase();
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
