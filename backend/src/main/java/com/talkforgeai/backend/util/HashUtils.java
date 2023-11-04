/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
