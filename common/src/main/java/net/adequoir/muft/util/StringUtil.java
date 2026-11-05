package net.adequoir.muft.util;

public final class StringUtil {
    public static String lowercaseFirst(String string) {
        if (string.isBlank()) return string;
        if (string.length() == 1) return Character.toString(string.charAt(0));
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static boolean containsInsensitive(String source, String target) {
        if (source.isBlank()) return target.isBlank();
        if (target.isBlank()) return true;
        if (source.length() < target.length()) return false;
        if (source.length() == target.length()) return source.equalsIgnoreCase(target);

        int i = 0;
        final int end = source.length() - target.length();
        char firstChar = Character.toLowerCase(target.charAt(0));

        while (i <= end) {
            boolean bl = Character.toLowerCase(source.charAt(i)) == firstChar
                    && matchesAtInsensitive(source, i, target);
            if (bl) return true;
            i++;
        }
        return false;
    }

    public static boolean matchesAtInsensitive(String source, int offset, String target) {
        int i = offset;
        int j = 0;

        while (j < target.length()) {
            if (Character.toLowerCase(source.charAt(i)) != Character.toLowerCase(target.charAt(j))) return false;
            i++; j++;
        }
        return true;
    }

    private StringUtil() {}
}
