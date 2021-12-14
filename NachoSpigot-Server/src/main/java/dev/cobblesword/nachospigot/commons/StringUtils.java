package dev.cobblesword.nachospigot.commons;

public class StringUtils {
    public static String repeat(String string, int count) {
        return new String(new char[count]).replace("\0", string);
    }

    public static int getIndentation(String s) {
        if (!s.startsWith(" ")) return 0;
        int i = 0;
        while ((s = s.replaceFirst(" ", "")).startsWith(" ")) i++;
        return i + 1;
    }
}
