package com.example.batchservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Helpers {

	private Helpers() {

	}

//	This method capitalizes the first character of a given string
	public static String capitalizeFirstCharacter(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);

		return String.valueOf(chars);
	}

//	This method converts a string with words separated by spaces into camel case
	public static String convertWordsToCamelCase(String str) {
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

//	This method capitalizes the first character of each word in a string with words separated by spaces
	public static String capitalizeEachWord(String str) {
		str = insertSpacesInCamelCase(str);
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (i == 0 || chars[i - 1] == ' ') {
				chars[i] = Character.toUpperCase(chars[i]);
			}
		}
		return String.valueOf(chars);
	}

//	This method inserts spaces before uppercase letters in a camel case string
	public static String insertSpacesInCamelCase(String str) {
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

//	This method converts a string to title case, where the first letter of each word is capitalized
	public static String convertToTitleCase(String input) {
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
