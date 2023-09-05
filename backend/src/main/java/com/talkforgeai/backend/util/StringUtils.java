package com.talkforgeai.backend.util;

public class StringUtils {
    public static String maxLengthString(String input, int maxLength) {
        if (maxLength < 0) {
            return "";
        }
        return input.substring(0, Math.min(input.length(), maxLength));
    }
}
