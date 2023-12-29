package com.example.createtemplateservice.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

	public static String getCurrentDateTime() throws Exception {
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
			throw new Exception("*Exception in getCurrentDateTime **" + e);
		}
		return strSysDate;
	}

}
