package app.revanced.integrations.utils;

import static app.revanced.integrations.utils.StringRef.str;

import android.content.pm.PackageManager;

import java.util.Locale;

public class ReVancedHelper {
    private static boolean RTL = false;
    private static boolean isFounded = false;
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

    private ReVancedHelper() {
    } // utility class

    public static boolean isTablet() {
        var context = ReVancedUtils.getContext();
        assert context != null;
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

    public static void setRTL() {
        if (isFounded) return;
        try {
            final int directionality = Character.getDirectionality(Locale.getDefault().getDisplayName().charAt(0));
            RTL = directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
            isFounded = true;
        } catch (Exception ex) {
            LogHelper.printException(ReVancedUtils.class, "Failed to get locale", ex);
            RTL = false;
        }
    }

    public static String getOldString(String oldstring) {
        String hiddenMessageString = str("revanced_ryd_video_likes_hidden_by_video_owner");
        if (!Character.isDigit(oldstring.charAt(0)))
            return hiddenMessageString;

        if (oldstring.contains("?"))
            oldstring = RTL ? oldstring.split(" \\? ")[1] : oldstring.split(" \\? ")[0];

        return oldstring;
    }

    public static String setRTLString(String likeString, String dislikeString) {
        return RTL ? String.format("%s ? %s", dislikeString, likeString) : String.format("%s ? %s", likeString, dislikeString);
    }

    public static String getVersionName() {
        try {
            var context = ReVancedUtils.getContext();
            assert context != null;
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "17.49.37";
    }
}
