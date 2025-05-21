package backend.academy.utils;

import org.jetbrains.annotations.Nullable;

public class StringUtils {
    public static String shortPreview(@Nullable String str, int length) {
        if (str == null) return "";
        return (str.length() > length) ? str.substring(0, 200) + "..." : str;
    }

    public static String shortPreview(String str) {
        return shortPreview(str, 200);
    }
}
