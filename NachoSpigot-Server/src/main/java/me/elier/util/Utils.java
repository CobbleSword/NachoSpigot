package me.elier.util;

import org.apache.commons.lang.reflect.FieldUtils;

import java.util.Arrays;

public class Utils {
    // from String.repeat in Java 11
    public static String repeat(String string, int count) {
        byte[] value = null;
        byte coder = 0;
        try {
            value = (byte[]) FieldUtils.readDeclaredField(string, "value", true);
            coder = (byte) FieldUtils.readDeclaredField(string, "coder", true);
        } catch (IllegalAccessException ignored) {}
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (count == 1) {
            return string;
        }
        final int len = value.length;
        if (len == 0 || count == 0) {
            return "";
        }
        if (Integer.MAX_VALUE / count < len) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        if (len == 1) {
            final byte[] single = new byte[count];
            Arrays.fill(single, value[0]);
            return new String(single, coder);
        }
        final int limit = len * count;
        final byte[] multiple = new byte[limit];
        System.arraycopy(value, 0, multiple, 0, len);
        int copied = len;
        for (; copied < limit - copied; copied <<= 1) {
            System.arraycopy(multiple, 0, multiple, copied, copied);
        }
        System.arraycopy(multiple, 0, multiple, copied, limit - copied);
        return new String(multiple, coder);
    }
}
