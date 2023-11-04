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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

    @Test
    public void testMaxLengthString() {
        assertEquals("12345", StringUtils.maxLengthString("12345", 10));
        assertEquals("12345", StringUtils.maxLengthString("1234567890", 5));
        assertEquals("12345", StringUtils.maxLengthString("12345", 5));
    }

    @Test
    public void testMaxLengthStringWithNull() {
        assertEquals("", StringUtils.maxLengthString(null, 10));
    }

    @Test
    public void testMaxLengthStringWithNegativeLength() {
        assertEquals("", StringUtils.maxLengthString("12345", -1));
    }

    @Test
    public void testMaxLengthStringWithZeroLength() {
        assertEquals("", StringUtils.maxLengthString("12345", 0));
    }

    @Test
    public void testMaxLengthStringWithMaxLengthGreaterThanStringLength() {
        assertEquals("12345", StringUtils.maxLengthString("12345", 10));
    }

    @Test
    public void testMaxLengthStringWithMaxLengthLessThanStringLength() {
        assertEquals("12345", StringUtils.maxLengthString("1234567890", 5));
    }

    @Test
    public void testMaxLengthStringWithMaxLengthEqualToStringLength() {
        assertEquals("12345", StringUtils.maxLengthString("12345", 5));
    }

}