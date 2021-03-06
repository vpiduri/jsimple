/*
 * Copyright (c) 2012-2015, Microsoft Mobile
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jsimple.net;

import jsimple.unit.UnitTest;
import org.junit.Test;

/**
 * @author Bret Johnson
 * @since 11/25/12 10:58 PM
 */
public class UrlEncoderTest extends UnitTest {
    @Test public void testEncode() {
        assertEquals(".-_*", UrlEncoder.encode(".-_*"));   // None encoded
        assertEquals("%24%2B%21%27%28%29%2C", UrlEncoder.encode("$+!'(),"));   // All encoded
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                UrlEncoder.encode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));   // None encoded
        assertEquals("x%C3%AB%C4%86%D2%B8abc%EF%AE%9A", UrlEncoder.encode("x\u00eb\u0106\u04b8abc\ufb9a"));
        assertEquals("abc%3Ddef%3Fsdf%26hil+sdlkfj+l+sdf+slkdfj+123%26def",
                UrlEncoder.encode("abc=def?sdf&hil sdlkfj l sdf slkdfj 123&def"));
    }
}
