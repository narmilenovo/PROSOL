package com.example.user_management.utils;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.user_management.exceptions.DateTimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Helpers {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private Helpers() {

	}

	public static String firstLetterCaps(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static void inputTitleCase(Object object, List<String> fieldsToSkipCapitalization) {
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				if (value instanceof String string && !fieldsToSkipCapitalization.contains(field.getName())) {
					String firstCaps = firstLetterCaps(string);
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
					String firstCaps = firstLetterCaps(string);
					field.set(object, firstCaps);
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		}
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

	public static String generateRandomString(int length) {
		String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder result = new StringBuilder(length);
		Random random = new SecureRandom();

		for (int i = 0; i < length; i++) {
			int position = random.nextInt(possibleChars.length());
			result.append(possibleChars.charAt(position));
		}

		return result.toString();
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

	public static Boolean checkNotNull(Object strNull) {
		return strNull != null && !strNull.equals("");
	}

	public static String getCurrentDateTime() throws DateTimeException {
		Calendar cal = Calendar.getInstance();
		String strSysDate = "";
		try {
			String strSysDay = String.valueOf(cal.get(Calendar.DATE));
			if (Integer.parseInt(strSysDay) < 10) {
				strSysDay = "0" + strSysDay;
			}

			String strSysMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
			if (Integer.parseInt(strSysMonth) < 10) {
				strSysMonth = "0" + strSysMonth;
			}

			String strSysYear = String.valueOf(cal.get(Calendar.YEAR));

			strSysDate = strSysDay + "/" + strSysMonth + "/" + strSysYear;

		} catch (Exception e) {
			throw new DateTimeException("*Exception in getCurrentDateTime **" + e);
		}
		return strSysDate;
	}

	public static String convertJsonToString(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

}
