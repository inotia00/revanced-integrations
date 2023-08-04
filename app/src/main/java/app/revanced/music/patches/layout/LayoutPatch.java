package app.revanced.music.patches.layout;

import static app.revanced.music.utils.ReVancedUtils.hideViewBy0dpUnderCondition;
import static app.revanced.music.utils.ReVancedUtils.hideViewUnderCondition;

import android.view.View;
import android.widget.TextView;

import app.revanced.music.settings.SettingsEnum;

public class LayoutPatch {

    public static boolean disableAutoCaptions(boolean original) {
        return SettingsEnum.DISABLE_AUTO_CAPTIONS.getBoolean() || original;
    }

    public static int enableBlackNavigationBar() {
        return SettingsEnum.ENABLE_BLACK_NAVIGATION_BAR.getBoolean() ? -16777216 : -14869219;
    }

    public static boolean enableColorMatchPlayer() {
        return SettingsEnum.ENABLE_COLOR_MATCH_PLAYER.getBoolean();
    }

    public static int enableCompactDialog(int original) {
        return SettingsEnum.ENABLE_COMPACT_DIALOG.getBoolean() && original < 600 ? 600 : original;
    }

    public static boolean enableForceMinimizedPlayer(boolean original) {
        return SettingsEnum.ENABLE_FORCE_MINIMIZED_PLAYER.getBoolean() || original;
    }

    public static boolean enableLandScapeMode(boolean original) {
        try {
            return SettingsEnum.ENABLE_LANDSCAPE_MODE.getBoolean() || original;
        } catch (Exception ignored) {
            return original;
        }
    }

    public static boolean enableNewLayout() {
        return SettingsEnum.ENABLE_NEW_LAYOUT.getBoolean();
    }

    public static boolean enableOldStyleMiniPlayer(boolean original) {
        return !SettingsEnum.ENABLE_OLD_STYLE_MINI_PLAYER.getBoolean() && original;
    }

    public static boolean enableSleepTimer() {
        return SettingsEnum.ENABLE_SLEEP_TIMER.getBoolean();
    }

    public static boolean enableZenMode() {
        return SettingsEnum.ENABLE_ZEN_MODE.getBoolean();
    }

    public static int hideCastButton(int original) {
        return SettingsEnum.HIDE_CAST_BUTTON.getBoolean() ? View.GONE : original;
    }

    public static void hideCategoryBar(View view) {
        hideViewBy0dpUnderCondition(SettingsEnum.HIDE_CATEGORY_BAR.getBoolean(), view);
    }

    public static void hideNavigationLabel(TextView textview) {
        hideViewUnderCondition(SettingsEnum.HIDE_NAVIGATION_LABEL.getBoolean(), textview);
    }

    public static boolean hideNewPlaylistButton() {
        return SettingsEnum.HIDE_NEW_PLAYLIST_BUTTON.getBoolean();
    }

}
