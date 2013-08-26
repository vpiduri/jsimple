package jsimple.net;

import jsimple.io.IOUtils;
import jsimple.util.ByteArrayRange;
import jsimple.util.TextualPath;

/**
 * @author Bret Johnson
 * @since 11/25/12 9:03 PM
 */
public class UrlEncoder {
    /**
     * Encodes the given string {@code s} in a x-www-form-urlencoded string using UTF8 character encoding.
     * <p/>
     * All characters except letters ('a'..'z', 'A'..'Z') and numbers ('0'..'9') and characters '.', '-', '*', '_' are
     * converted into their hexadecimal value prepended by '%'. For example: '#' -> %23. In addition, spaces are
     * substituted by '+'
     *
     * @param s the string to be encoded.
     * @return the encoded string.
     * @throws java.io.UnsupportedEncodingException
     *          if the specified encoding scheme is invalid.
     */
    public static String encode(String s) {
        // Guess a bit bigger for encoded form
        StringBuilder buffer = new StringBuilder(s.length() + 16);
        int start = -1;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') ||
                    (ch >= '0' && ch <= '9') || " .-*_".indexOf(ch) > -1) {
                if (start >= 0) {
                    convert(s.substring(start, i), buffer);
                    start = -1;
                }
                if (ch != ' ')
                    buffer.append(ch);
                else buffer.append('+');
            } else {
                if (start < 0) {
                    start = i;
                }
            }
        }

        if (start >= 0)
            convert(s.substring(start, s.length()), buffer);

        return buffer.toString();
    }

    public static String encode(TextualPath path) {
        if (path.isEmpty())
            return "/";
        else {
            StringBuilder encodedPath = new StringBuilder();
            for (String pathElement : path.getPathElements()) {
                encodedPath.append("/");
                encodedPath.append(encode(pathElement));
            }
            return encodedPath.toString();
        }
    }

    private static final String digits = "0123456789ABCDEF";

    private static void convert(String s, StringBuilder buffer) {
        ByteArrayRange byteArrayRange = IOUtils.toUtf8BytesFromString(s);
        int length = byteArrayRange.getLength();
        byte[] bytes = byteArrayRange.getBytes();

        for (int j = byteArrayRange.getPosition(); j < length; j++) {
            buffer.append('%');
            byte currByte = bytes[j];
            buffer.append(digits.charAt((currByte & 0xf0) >> 4));
            buffer.append(digits.charAt(currByte & 0xf));
        }
    }
}
