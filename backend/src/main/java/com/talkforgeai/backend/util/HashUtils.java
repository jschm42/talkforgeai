package com.talkforgeai.backend.util;

import java.util.Random;

public class HashUtils {

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length the length of the generated string.
     * @return a random alphanumeric string.
     */
    public static String generateRandomCode(int length) {
        // Define the characters to use in the random string.
        String charSet = "abcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder randomCode = new StringBuilder(length);
        Random random = new Random();

        // Generate the random string.
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            randomCode.append(charSet.charAt(randomIndex));
        }

        return randomCode.toString();
    }
}
