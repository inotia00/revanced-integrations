package app.revanced.integrations.youtube.utils;

import static app.revanced.integrations.shared.utils.ResourceUtils.getColor;
import static app.revanced.integrations.shared.utils.ResourceUtils.getDrawable;
import static app.revanced.integrations.shared.utils.ResourceUtils.getStyleIdentifier;

import android.graphics.drawable.Drawable;

@SuppressWarnings("unused")
public class ThemeUtils {
    private static int themeValue;

    /**
     * Injection point.
     */
    public static void setTheme(Enum<?> themeEnum) {
        themeValue = themeEnum.ordinal();
    }

    public static boolean isDarkTheme() {
        return themeValue == 1;
    }

    public static int getThemeId() {
        final String themeName = isDarkTheme()
                ? "Theme.YouTube.Settings.Dark"
                : "Theme.YouTube.Settings";

        return getStyleIdentifier(themeName);
    }

    public static Drawable getBackButtonDrawable() {
        final String drawableName = isDarkTheme()
                ? "yt_outline_arrow_left_white_24"
                : "yt_outline_arrow_left_black_24";

        return getDrawable(drawableName);
    }

    public static Drawable getTrashButtonDrawable() {
        final String drawableName = isDarkTheme()
                ? "yt_outline_trash_can_white_24"
                : "yt_outline_trash_can_black_24";

        return getDrawable(drawableName);
    }

    public static int getTextColor() {
        final String colorName = isDarkTheme()
                ? "yt_white1"
                : "yt_black1";

        return getColor(colorName);
    }

    /**
     * Since {@link android.widget.Toolbar} is used instead of {@link android.support.v7.widget.Toolbar},
     * We have to manually specify the toolbar background.
     * @return toolbar background color.
     */
    public static int getToolbarBackgroundColor() {
        final String colorName = isDarkTheme()
                ? "yt_black3"   // Color names used in the light theme
                : "yt_white1";  // Color names used in the dark theme

        return getColor(colorName);
    }
}
