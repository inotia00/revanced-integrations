package app.revanced.integrations.utils;

public class ThemeHelperClass {
    private static int themeValue;

    public static void setTheme(int value) {
        themeValue = value;
    }

    public static void setTheme(Object value) {
        themeValue = ((Enum) value).ordinal();
    }

    public static boolean isDarkTheme() {
        return themeValue == 1;
    }

}
