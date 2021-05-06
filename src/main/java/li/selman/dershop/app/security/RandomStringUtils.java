/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package li.selman.dershop.app.security;

import java.util.Random;

/**
 * <p>Generates random {@code String}s.</p>
 *
 * <p><b>Caveat: Instances of {@link Random}, upon which the implementation of this
 * class relies, are not cryptographically secure.</b></p>
 *
 * <p>RandomStringUtils is intended for simple use cases. For more advanced
 * use cases consider using Apache Commons Text's
 * <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/RandomStringGenerator.html">
 * RandomStringGenerator</a> instead.</p>
 *
 * <p>The Apache Commons project provides
 * <a href="https://commons.apache.org/rng">Commons RNG</a> dedicated to pseudo-random number generation, that may be
 * a better choice for applications with more stringent requirements
 * (performance and/or correctness).</p>
 *
 * <p>Note that <em>private high surrogate</em> characters are ignored.
 * These are Unicode characters that fall between the values 56192 (db80)
 * and 56319 (dbff) as we don't know how to handle them.
 * High and low surrogates are correctly dealt with - that is if a
 * high surrogate is randomly chosen, 55296 (d800) to 56191 (db7f)
 * then it is followed by a low surrogate. If a low surrogate is chosen,
 * 56320 (dc00) to 57343 (dfff) then it is placed after a randomly
 * chosen high surrogate.</p>
 *
 * <p>#ThreadSafe#</p>
 * @since 1.0
 */
class RandomStringUtils {

    private static final String EMPTY = "";

    /**
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     *
     * <p>If start and end are both {@code 0}, start and end are set
     * to {@code ' '} and {@code 'z'}, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * {@code false}, in which case, start and end are set to
     * {@code 0} and {@link Character#MAX_CODE_POINT}.
     *
     * <p>If set is not {@code null}, characters between start and
     * end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count  the length of random string to create
     * @param start  the position in set of chars to start at (inclusive)
     * @param end  the position in set of chars to end before (exclusive)
     * @param letters  if {@code true}, generated string may include
     *  alphabetic characters
     * @param numbers  if {@code true}, generated string may include
     *  numeric characters
     * @param chars  the set of chars to choose randoms from, must not be empty.
     *  If {@code null}, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *  {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException if {@code count} &lt; 0 or the provided chars array is empty.
     * @since 2.0
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    static String random(int count, int start, int end, final boolean letters, final boolean numbers,
                         final char[] chars, final Random random) {

        int c = count;
        int s = start;
        int e = end;

        if (c == 0) {
            return EMPTY;
        }
        if (c < 0) {
            throw new IllegalArgumentException("Requested random string length " + c + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (s == 0 && e == 0) {
            if (chars != null) {
                e = chars.length;
            } else if (!letters && !numbers) {
                e = Character.MAX_CODE_POINT;
            } else {
                e = 'z' + 1;
                s = ' ';
            }
        } else if (e <= s) {
            throw new IllegalArgumentException("Parameter end (" + e + ") must be greater than start (" + s + ")");
        }

        final int zeroDigitAscii = 48;
        final int firstLetterAscii = 65;

        if (chars == null && (numbers && e <= zeroDigitAscii
            || letters && e <= firstLetterAscii)) {
            throw new IllegalArgumentException("Parameter end (" + e + ") must be greater then (" + zeroDigitAscii
                + ") for generating digits " + "or greater then (" + firstLetterAscii + ") for generating letters.");
        }

        final StringBuilder builder = new StringBuilder(c);
        final int gap = e - s;

        while (c-- != 0) {
            final int codePoint;
            if (chars == null) {
                codePoint = random.nextInt(gap) + s;

                switch (Character.getType(codePoint)) {
                    case Character.UNASSIGNED:
                    case Character.PRIVATE_USE:
                    case Character.SURROGATE:
                        c++;
                        continue;
                }

            } else {
                codePoint = chars[random.nextInt(gap) + s];
            }

            final int numberOfChars = Character.charCount(codePoint);
            if (c == 0 && numberOfChars > 1) {
                c++;
                continue;
            }

            if (letters && Character.isLetter(codePoint)
                || numbers && Character.isDigit(codePoint)
                || !letters && !numbers) {
                builder.appendCodePoint(codePoint);

                if (numberOfChars == 2) {
                    c--;
                }

            } else {
                c++;
            }
        }
        return builder.toString();
    }

    /**
     * <p>{@code RandomStringUtils} instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * {@code RandomStringUtils.random(5);}.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    private RandomStringUtils() {
    }

}
