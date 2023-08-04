package app.revanced.music.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.music.settings.SettingsEnum.ReturnType.BOOLEAN;
import static app.revanced.music.settings.SettingsEnum.ReturnType.INTEGER;
import static app.revanced.music.settings.SettingsEnum.ReturnType.STRING;
import static app.revanced.music.utils.SharedPrefHelper.getPreferences;
import static app.revanced.music.utils.SharedPrefHelper.saveBoolean;
import static app.revanced.music.utils.SharedPrefHelper.saveInteger;
import static app.revanced.music.utils.SharedPrefHelper.saveString;

import androidx.annotation.NonNull;

import java.util.Objects;

public enum SettingsEnum {

    // Ads
    HIDE_MUSIC_ADS("revanced_hide_music_ads", BOOLEAN, TRUE, true),


    // Layout
    CUSTOM_FILTER("revanced_custom_filter", BOOLEAN, FALSE),
    CUSTOM_FILTER_STRINGS("revanced_custom_filter_strings", STRING, "", true),
    DISABLE_AUTO_CAPTIONS("revanced_disable_auto_captions", BOOLEAN, FALSE),
    ENABLE_BLACK_NAVIGATION_BAR("revanced_enable_black_navigation_bar", BOOLEAN, TRUE),
    ENABLE_COLOR_MATCH_PLAYER("revanced_enable_color_match_player", BOOLEAN, TRUE),
    ENABLE_COMPACT_DIALOG("revanced_enable_compact_dialog", BOOLEAN, TRUE),
    ENABLE_FORCE_MINIMIZED_PLAYER("revanced_enable_force_minimized_player", BOOLEAN, TRUE),
    ENABLE_LANDSCAPE_MODE("revanced_enable_landscape_mode", BOOLEAN, TRUE, true),
    ENABLE_NEW_LAYOUT("revanced_enable_new_layout", BOOLEAN, TRUE, true),
    ENABLE_OLD_STYLE_MINI_PLAYER("revanced_enable_old_style_mini_player", BOOLEAN, FALSE, true),
    ENABLE_SLEEP_TIMER("revanced_enable_sleep_timer", BOOLEAN, TRUE, true),
    ENABLE_ZEN_MODE("revanced_enable_zen_mode", BOOLEAN, FALSE),
    HIDE_BUTTON_SHELF("revanced_hide_button_shelf", BOOLEAN, FALSE, true),
    HIDE_CAROUSEL_SHELF("revanced_hide_carousel_shelf", BOOLEAN, FALSE, true),
    HIDE_CAST_BUTTON("revanced_hide_cast_button", BOOLEAN, TRUE),
    HIDE_CATEGORY_BAR("revanced_hide_category_bar", BOOLEAN, TRUE, true),
    HIDE_CHANNEL_GUIDELINES("revanced_hide_channel_guidelines", BOOLEAN, TRUE),
    HIDE_NAVIGATION_LABEL("revanced_hide_navigation_label", BOOLEAN, FALSE, true),
    HIDE_NEW_PLAYLIST_BUTTON("revanced_hide_new_playlist_button", BOOLEAN, FALSE),
    HIDE_PLAYLIST_CARD("revanced_hide_playlist_card", BOOLEAN, FALSE, true),


    // Misc
    ENABLE_DEBUG_LOGGING("revanced_enable_debug_logging", BOOLEAN, FALSE),
    ENABLE_FORCE_SHUFFLE("revanced_enable_force_shuffle", BOOLEAN, TRUE),
    ENABLE_OPUS_CODEC("revanced_enable_opus_codec", BOOLEAN, TRUE, true),
    ENABLE_SAVE_VIDEO_QUALITY("revanced_enable_save_video_quality", BOOLEAN, TRUE),
    EXTERNAL_DOWNLOADER_PACKAGE_NAME("revanced_external_downloader_package_name", STRING, "com.junkfood.seal", true),
    DEFAULT_VIDEO_QUALITY_WIFI("revanced_default_video_quality_wifi", INTEGER, -2),
    DEFAULT_VIDEO_QUALITY_MOBILE("revanced_default_video_quality_mobile", INTEGER, -2),
    HOOK_SHARE_BUTTON("revanced_hook_share_button", BOOLEAN, FALSE, true),
    SPOOF_APP_VERSION("revanced_spoof_app_version", BOOLEAN, FALSE, true);

    static {
        loadAllSettings();
    }

    @NonNull
    public final String path;
    @NonNull
    public final Object defaultValue;
    @NonNull
    public final ReturnType returnType;

    public final boolean rebootApp;

    public Object value;

    SettingsEnum(@NonNull String path, @NonNull ReturnType returnType, @NonNull Object defaultValue) {
        this(path, returnType, defaultValue, false);
    }

    SettingsEnum(@NonNull String path, @NonNull ReturnType returnType, @NonNull Object defaultValue, boolean rebootApp) {
        this.path = path;
        this.returnType = returnType;
        this.defaultValue = defaultValue;
        this.rebootApp = rebootApp;
    }

    private static void loadAllSettings() {
        for (SettingsEnum setting : values()) {
            setting.load();
        }
    }

    private void load() {
        switch (returnType) {
            case BOOLEAN ->
                    value = Objects.requireNonNull(getPreferences()).getBoolean(path, (boolean) defaultValue);
            case INTEGER ->
                    value = Objects.requireNonNull(getPreferences()).getInt(path, (Integer) defaultValue);
            case STRING ->
                    value = Objects.requireNonNull(getPreferences()).getString(path, (String) defaultValue);
            default -> throw new IllegalStateException(name());
        }
    }

    public void saveValue(@NonNull Object newValue) {
        Objects.requireNonNull(newValue);
        switch (returnType) {
            case BOOLEAN -> saveBoolean(path, (boolean) newValue);
            case INTEGER -> saveInteger(path, (Integer) newValue);
            default -> saveString(path, newValue.toString());
        }
        value = newValue;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public int getInt() {
        return (Integer) value;
    }

    public String getString() {
        return (String) value;
    }

    public enum ReturnType {
        BOOLEAN,
        INTEGER,
        STRING,
    }
}
