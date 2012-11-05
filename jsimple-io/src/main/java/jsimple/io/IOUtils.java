package jsimple.io;

/**
 * @author Bret Johnson
 * @since 10/13/12 4:26 PM
 */
public class IOUtils {
    /**
     * Converts the string to a UTF-8 byte array.  The array is returned, but it can be bigger than is otherwise needed.
     * Only the first length[0] bytes of the array should be used.
     *
     * @param s      string input
     * @param length int[1] array, returning number of bytes at beginning of array containing the data
     * @return byte array, for the UTF-8 encoded string
     */
    public static byte[] toUtf8BytesFromString(String s, int[] length) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(s.length());

        Utf8OutputStreamWriter utf8OutputStreamWriter = new Utf8OutputStreamWriter(byteArrayOutputStream);
        utf8OutputStreamWriter.write(s);
        utf8OutputStreamWriter.flush();

        byte[] bytes = byteArrayOutputStream.getByteArray(length);

        utf8OutputStreamWriter.close();
        return bytes;
    }

    /**
     * Converts the string to a UTF-8 byte array, which is returned.  Unlike the method above, which returns the
     * length[0] bytes to use, the entire array array should be used here.  This method can be slightly less efficient
     * than the above version, since a copy of the array is made in some cases, but the tradeoff is more convenience. If
     * the input string only contains Latin1 characters, then both methods are the same, as no copy is made since we can
     * easily predict the exact needed byte array length before hand.
     *
     * @param s string input
     * @return byte array, for the UTF-8 encoded string
     */
    public static byte[] toUtf8BytesFromString(String s) {
        int[] length = new int[1];
        byte[] bytes = toUtf8BytesFromString(s, length);

        if (bytes.length == length[0])
            return bytes;
        else {
            byte[] copy = new byte[length[0]];
            System.arraycopy(bytes, 0, copy, 0, length[0]);
            return copy;
        }
    }

    /**
     * Converts the stream contents, assumed to be a UTF-8 character data, to a string.  The inputStream is closed on
     * success.  If an exception is thrown,  the caller should close() inputStream (e.g. in a finally clause, since
     * calling close multiple times is benign).
     *
     * @param inputStream input stream, assumed to be UTF-8
     * @return string
     */

    /**
     * Convert the subset of the byte array, from position through position + length - 1, assumed to be a UTF-8
     * character data, to a string.
     *
     * @param utf8Bytes UTF-8 byte array
     * @param position  starting position in array
     * @param length    number of bytes to read
     * @return String for UTF-8 data
     */
    public static String toStringFromUtf8Bytes(byte[] utf8Bytes, int position, int length) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(utf8Bytes, 0, length);
        return toStringFromReader(new Utf8InputStreamReader(byteArrayInputStream));
    }

    /**
     * Convert a byte array, assumed to be a Latin1 (aka 8859-1 aka Windows 1252) encoded string, to a regular Java
     * string.  Latin1 is the first 256 characters of Unicode, so this conversion is just a straight pass through,
     * turning bytes in chars.  Note that ASCII is a subset of Latin1.
     *
     * @param bytes byte array
     * @return resulting string
     */
    public static String toStringFromLatin1Bytes(byte[] bytes) {
        final int length = bytes.length;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++)
            chars[i] = (char) bytes[i];
        return new String(chars);
    }

    /**
     * Convert a string, assumed to contain only Latin1 (aka 8859-1 aka Windows 1252) characters, to a byte array.  The
     * byte array is encoded one byte per character--just a straight pass through from the Unicode, dropping the high
     * byte (which should be zero) of each character.  If any non-Latin1 characters are in the string an exception is
     * thrown.  This method is an efficient way to only ASCII text as a byte array, since ASCII is a subset of Latin1.
     *
     * @param string input string, which should contain only Latin1 characters
     * @return resulting byte array
     */
    public static byte[] toLatin1BytesFromString(String string) {
        final int length = string.length();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c >= 256)
                throw new RuntimeException("Character '" + c + "' at index + " + i + " isn't a Latin1 character");
            bytes[i] = (byte) c;
        }
        return bytes;
    }

    /**
     * Converts the stream contents, assumed to be a UTF-8 character data, to a string.  The inputStream is closed on
     * success.  If an exception is thrown,  the caller should close() inputStream (e.g. in a finally clause, since
     * calling close multiple times is benign).
     *
     * @param inputStream input stream, assumed to be UTF-8
     * @return string
     */
    public static String toStringFromUtf8Stream(InputStream inputStream) {
        return toStringFromReader(new Utf8InputStreamReader(inputStream));
    }

    /**
     * Converts the reader contents to a string.  The reader is closed on success.  If an exception is thrown,  the
     * caller should close() the reader or at least close the input stream that it's based on (e.g. it may close it in a
     * finally clause, since calling close multiple times is benign).
     *
     * @param reader reader object
     * @return string contents of reader
     */
    public static String toStringFromReader(Reader reader) {
        final char[] buffer = new char[8*1024];
        StringBuilder out = new StringBuilder();
        int charsRead;
        do {
            charsRead = reader.read(buffer, 0, buffer.length);
            if (charsRead > 0)
                out.append(buffer, 0, charsRead);
        } while (charsRead >= 0);
        reader.close();
        return out.toString();
    }

    /**
     * Read the remaining data from the input stream into a byte array, which is returned.  Only the first length[0]
     * bytes of the byte array should be used.  The inputStream is closed after it's completely read.
     *
     * @param inputStream input stream
     * @param length      int[1] array; length of the data is returned in length[0]
     * @return byte array contain the data in the stream, in the first length[0] bytes
     */
    public static byte[] toBytesFromStream(InputStream inputStream, int[] length) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(inputStream);
        return byteStream.getByteArray(length);
    }

    /**
     * Read the remaining data from the input stream into a byte array, which is returned.  inputStream is closed after
     * it's completely read.  Unlike the version with the length parameter, this method returns an array of exactly the
     * right size, containing just the data in question and no more.  However, that convenience comes at the expense of
     * performance, as normally an extra copy of the array is required.
     *
     * @param inputStream input stream
     * @return byte array contain the data in the stream
     */
    public static byte[] toBytesFromStream(InputStream inputStream) {
        int[] length = new int[1];
        byte[] bytes = toBytesFromStream(inputStream, length);

        if (bytes.length == length[0])
            return bytes;
        else {
            byte[] copy = new byte[length[0]];
            System.arraycopy(bytes, 0, copy, 0, length[0]);
            return copy;
        }
    }
}
