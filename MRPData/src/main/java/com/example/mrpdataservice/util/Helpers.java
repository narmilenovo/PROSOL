package com.example.mrpdataservice.util;

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
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
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

    public static void updateErrorHashMap(
            Map<String, List<String>> errors, String field, String message) {
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

    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new NullPointerException("Input Id is null or less then zero");
        }
    }

    public static void validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty() || ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new NullPointerException("one of the Input Id's is null or less then zero");
        }
    }
}
