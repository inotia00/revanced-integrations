package app.revanced.music.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.music.settings.SettingsEnum.ReturnType.BOOLEAN;
import static app.revanced.music.settings.SettingsEnum.ReturnType.FLOAT;
import static app.revanced.music.settings.SettingsEnum.ReturnType.INTEGER;
import static app.revanced.music.settings.SettingsEnum.ReturnType.LONG;
import static app.revanced.music.settings.SettingsEnum.ReturnType.STRING;
import static app.revanced.music.utils.SharedPrefHelper.getPreferences;
import static app.revanced.music.utils.SharedPrefHelper.saveBoolean;
import static app.revanced.music.utils.SharedPrefHelper.saveFloat;
import static app.revanced.music.utils.SharedPrefHelper.saveInteger;
import static app.revanced.music.utils.SharedPrefHelper.saveLong;
import static app.revanced.music.utils.SharedPrefHelper.saveString;
import static app.revanced.music.utils.StringRef.str;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import app.revanced.music.utils.LogHelper;
import app.revanced.music.utils.ReVancedUtils;

public enum SettingsEnum {

    // Account
    HIDE_ACCOUNT_MENU("revanced_hide_account_menu", BOOLEAN, FALSE),
    HIDE_ACCOUNT_MENU_FILTER_STRINGS("revanced_hide_account_menu_filter_strings", STRING, ""),
    HIDE_ACCOUNT_MENU_EMPTY_COMPONENT("revanced_hide_account_menu_empty_component", BOOLEAN, FALSE),
    HIDE_HANDLE("revanced_hide_handle", BOOLEAN, TRUE, true),
    HIDE_TERMS_CONTAINER("revanced_hide_terms_container", BOOLEAN, FALSE, true),


    // Action Bar
    EXTERNAL_DOWNLOADER_PACKAGE_NAME("revanced_external_downloader_package_name", STRING, "com.deniscerri.ytdl", true),
    HIDE_ACTION_BAR_LABEL("revanced_hide_action_bar_label", BOOLEAN, FALSE),
    HIDE_ACTION_BAR_RADIO("revanced_hide_action_bar_radio", BOOLEAN, FALSE),
    HOOK_ACTION_BAR_DOWNLOAD("revanced_hook_action_bar_download", BOOLEAN, FALSE, true),


    // Ads
    HIDE_MUSIC_ADS("revanced_hide_music_ads", BOOLEAN, TRUE, true),


    // Flyout
    ENABLE_COMPACT_DIALOG("revanced_enable_compact_dialog", BOOLEAN, TRUE),
    ENABLE_SLEEP_TIMER("revanced_enable_sleep_timer", BOOLEAN, TRUE, true),
    ENABLE_FLYOUT_PANEL_PLAYBACK_SPEED("revanced_enable_flyout_panel_playback_speed", BOOLEAN, FALSE, true),

    HIDE_FLYOUT_PANEL_ADD_TO_QUEUE("revanced_hide_flyout_panel_add_to_queue", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_CAPTIONS("revanced_hide_flyout_panel_captions", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_DISMISS_QUEUE("revanced_hide_flyout_panel_dismiss_queue", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_DOWNLOAD("revanced_hide_flyout_panel_download", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_EDIT_PLAYLIST("revanced_hide_flyout_panel_edit_playlist", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_GO_TO_ALBUM("revanced_hide_flyout_panel_go_to_album", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_GO_TO_ARTIST("revanced_hide_flyout_panel_go_to_artist", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_GO_TO_EPISODE("revanced_hide_flyout_panel_go_to_episode", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_GO_TO_PODCAST("revanced_hide_flyout_panel_go_to_podcast", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_LIKE_DISLIKE("revanced_hide_flyout_panel_like_dislike", BOOLEAN, FALSE, true),
    HIDE_FLYOUT_PANEL_PLAY_NEXT("revanced_hide_flyout_panel_play_next", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_QUALITY("revanced_hide_flyout_panel_quality", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_REMOVE_FROM_LIBRARY("revanced_hide_flyout_panel_remove_from_library", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_REPORT("revanced_hide_flyout_panel_report", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SAVE_EPISODE_FOR_LATER("revanced_hide_flyout_panel_save_episode_for_later", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SAVE_TO_LIBRARY("revanced_hide_flyout_panel_save_to_library", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SAVE_TO_PLAYLIST("revanced_hide_flyout_panel_save_to_playlist", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SHARE("revanced_hide_flyout_panel_share", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SHUFFLE("revanced_hide_flyout_panel_shuffle", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_SLEEP_TIMER("revanced_hide_flyout_panel_sleep_timer", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_START_RADIO("revanced_hide_flyout_panel_start_radio", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_STATS_FOR_NERDS("revanced_hide_flyout_panel_stats_for_nerds", BOOLEAN, FALSE),
    HIDE_FLYOUT_PANEL_VIEW_SONG_CREDIT("revanced_hide_flyout_panel_view_song_credit", BOOLEAN, FALSE),
    REPLACE_FLYOUT_PANEL_DISMISS_QUEUE("revanced_replace_flyout_panel_dismiss_queue", BOOLEAN, FALSE, true),
    REPLACE_FLYOUT_PANEL_DISMISS_QUEUE_CONTINUE_WATCH("revanced_replace_flyout_panel_dismiss_queue_continue_watch", BOOLEAN, TRUE),


    // General
    CUSTOM_FILTER("revanced_custom_filter", BOOLEAN, FALSE),
    CUSTOM_FILTER_STRINGS("revanced_custom_filter_strings", STRING, "", true),
    DISABLE_AUTO_CAPTIONS("revanced_disable_auto_captions", BOOLEAN, FALSE),
    ENABLE_LANDSCAPE_MODE("revanced_enable_landscape_mode", BOOLEAN, TRUE, true),
    ENABLE_OLD_STYLE_LIBRARY_SHELF("revanced_enable_old_style_library_shelf", BOOLEAN, FALSE, true),
    HIDE_BUTTON_SHELF("revanced_hide_button_shelf", BOOLEAN, FALSE, true),
    HIDE_CAROUSEL_SHELF("revanced_hide_carousel_shelf", BOOLEAN, FALSE, true),
    HIDE_CAST_BUTTON("revanced_hide_cast_button", BOOLEAN, TRUE),
    HIDE_CATEGORY_BAR("revanced_hide_category_bar", BOOLEAN, FALSE, true),
    HIDE_CHANNEL_GUIDELINES("revanced_hide_channel_guidelines", BOOLEAN, TRUE),
    HIDE_EMOJI_PICKER("revanced_hide_emoji_picker", BOOLEAN, FALSE),
    HIDE_NEW_PLAYLIST_BUTTON("revanced_hide_new_playlist_button", BOOLEAN, FALSE),
    HIDE_PLAYLIST_CARD("revanced_hide_playlist_card", BOOLEAN, FALSE, true),
    START_PAGE("revanced_start_page", STRING, "FEmusic_home", true),


    // Misc
    ENABLE_DEBUG_LOGGING("revanced_enable_debug_logging", BOOLEAN, FALSE),
    ENABLE_OPUS_CODEC("revanced_enable_opus_codec", BOOLEAN, TRUE, true),
    SETTINGS_INITIALIZED("revanced_settings_initialized", BOOLEAN, FALSE),
    SPOOF_APP_VERSION("revanced_spoof_app_version", BOOLEAN, FALSE, true),
    SPOOF_APP_VERSION_TARGET("revanced_spoof_app_version_target", STRING, "4.27.53", true),

    // Navigation
    ENABLE_BLACK_NAVIGATION_BAR("revanced_enable_black_navigation_bar", BOOLEAN, TRUE),
    HIDE_EXPLORE_BUTTON("revanced_hide_explore_button", BOOLEAN, FALSE, true),
    HIDE_HOME_BUTTON("revanced_hide_home_button", BOOLEAN, FALSE, true),
    HIDE_LIBRARY_BUTTON("revanced_hide_library_button", BOOLEAN, FALSE, true),
    HIDE_NAVIGATION_BAR("revanced_hide_navigation_bar", BOOLEAN, FALSE, true),
    HIDE_NAVIGATION_LABEL("revanced_hide_navigation_label", BOOLEAN, FALSE, true),
    HIDE_SAMPLES_BUTTON("revanced_hide_samples_button", BOOLEAN, FALSE, true),
    HIDE_UPGRADE_BUTTON("revanced_hide_upgrade_button", BOOLEAN, TRUE, true),


    // Player
    ENABLE_COLOR_MATCH_PLAYER("revanced_enable_color_match_player", BOOLEAN, TRUE),
    ENABLE_FORCE_MINIMIZED_PLAYER("revanced_enable_force_minimized_player", BOOLEAN, TRUE),
    ENABLE_NEW_PLAYER_BACKGROUND("revanced_enable_new_player_background", BOOLEAN, FALSE, true),
    ENABLE_OLD_PLAYER_LAYOUT("revanced_enable_old_player_layout", BOOLEAN, FALSE, true),
    ENABLE_OLD_STYLE_MINI_PLAYER("revanced_enable_old_style_mini_player", BOOLEAN, TRUE, true),
    ENABLE_ZEN_MODE("revanced_enable_zen_mode", BOOLEAN, FALSE),
    REMEMBER_REPEAT_SATE("revanced_remember_repeat_state", BOOLEAN, TRUE),
    REMEMBER_SHUFFLE_SATE("revanced_remember_shuffle_state", BOOLEAN, TRUE),
    SHUFFLE_SATE("revanced_shuffle_state", INTEGER, 1),
    REPLACE_PLAYER_CAST_BUTTON("revanced_replace_player_cast_button", BOOLEAN, FALSE, true),


    // Video
    CUSTOM_PLAYBACK_SPEEDS("revanced_custom_playback_speeds", STRING,
            "0.25\n0.5\n0.75\n1.0\n1.25\n1.5\n1.75\n2.0", true),
    ENABLE_SAVE_PLAYBACK_SPEED("revanced_enable_save_playback_speed", BOOLEAN, FALSE),
    ENABLE_SAVE_VIDEO_QUALITY("revanced_enable_save_video_quality", BOOLEAN, TRUE),
    DEFAULT_PLAYBACK_SPEED("revanced_default_playback_speed", FLOAT, 1.0f),
    DEFAULT_VIDEO_QUALITY_MOBILE("revanced_default_video_quality_mobile", INTEGER, -2),
    DEFAULT_VIDEO_QUALITY_WIFI("revanced_default_video_quality_wifi", INTEGER, -2),


    // Return YouTube Dislike
    RYD_USER_ID("revanced_ryd_user_id", STRING, ""),
    RYD_ENABLED("revanced_ryd_enabled", BOOLEAN, TRUE),
    RYD_DISLIKE_PERCENTAGE("revanced_ryd_dislike_percentage", BOOLEAN, FALSE),
    RYD_COMPACT_LAYOUT("revanced_ryd_compact_layout", BOOLEAN, FALSE),


    // SponsorBlock
    SB_ENABLED("sb_enabled", BOOLEAN, TRUE),
    SB_TOAST_ON_SKIP("sb_toast_on_skip", BOOLEAN, TRUE),
    SB_API_URL("sb_api_url", STRING, "https://sponsor.ajay.app"),
    SB_PRIVATE_USER_ID("sb_private_user_id", STRING, ""),
    // SB settings not exported
    SB_LAST_VIP_CHECK("sb_last_vip_check", LONG, 0L);


    /**
     * If a setting path has this prefix, then remove it before importing/exporting.
     */
    private static final String OPTIONAL_REVANCED_SETTINGS_PREFIX = "revanced_";

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

    private static SettingsEnum[] valuesSortedForExport() {
        SettingsEnum[] sorted = values();
        Arrays.sort(sorted, Comparator.comparing((SettingsEnum o) -> o.path));
        return sorted;
    }

    @NonNull
    public static String exportJSON() {
        try {
            JSONObject json = new JSONObject();
            for (SettingsEnum setting : valuesSortedForExport()) {
                String importExportKey = setting.getImportExportKey();
                if (json.has(importExportKey)) {
                    throw new IllegalArgumentException("duplicate key found: " + importExportKey);
                }
                if (setting.includeWithImportExport() && !setting.isSetToDefault()) {
                    json.put(importExportKey, setting.getObjectValue());
                }
            }

            if (json.length() == 0) {
                return "";
            }
            String export = json.toString(0);
            // Remove the outer JSON braces to make the output more compact,
            // and leave less chance of the user forgetting to copy it
            return export.substring(2, export.length() - 2);
        } catch (JSONException e) {
            LogHelper.printException(SettingsEnum.class, "Export failure", e); // should never happen
            return "";
        }
    }

    public static void importJSON(@NonNull String settingsJsonString) {
        try {
            if (!settingsJsonString.matches("[\\s\\S]*\\{")) {
                settingsJsonString = '{' + settingsJsonString + '}'; // Restore outer JSON braces
            }
            JSONObject json = new JSONObject(settingsJsonString);

            int numberOfSettingsImported = 0;
            for (SettingsEnum setting : values()) {
                String key = setting.getImportExportKey();
                if (json.has(key)) {
                    Object value = switch (setting.returnType) {
                        case BOOLEAN -> json.getBoolean(key);
                        case INTEGER -> json.getInt(key);
                        case LONG -> json.getLong(key);
                        case FLOAT -> (float) json.getDouble(key);
                        case STRING -> json.getString(key);
                    };
                    if (!setting.getObjectValue().equals(value)) {
                        setting.saveValue(value);
                    }
                    numberOfSettingsImported++;
                } else if (setting.includeWithImportExport() && !setting.isSetToDefault()) {
                    LogHelper.printDebug(SettingsEnum.class, "Resetting to default: " + setting);
                    setting.saveValue(setting.defaultValue);
                }
            }

            ReVancedUtils.showToastShort(numberOfSettingsImported == 0
                    ? str("revanced_extended_settings_import_reset")
                    : str("revanced_extended_settings_import_success", numberOfSettingsImported));

        } catch (JSONException | IllegalArgumentException ex) {
            ReVancedUtils.showToastShort(str("revanced_extended_settings_import_failure_parse", ex.getMessage()));
            LogHelper.printException(SettingsEnum.class, "", ex);
        } catch (Exception ex) {
            LogHelper.printException(SettingsEnum.class, "Import failure: " + ex.getMessage(), ex); // should never happen
        }
    }

    private void load() {
        switch (returnType) {
            case BOOLEAN ->
                    value = Objects.requireNonNull(getPreferences()).getBoolean(path, (boolean) defaultValue);
            case INTEGER ->
                    value = Objects.requireNonNull(getPreferences()).getInt(path, (Integer) defaultValue);
            case LONG ->
                    value = Objects.requireNonNull(getPreferences()).getLong(path, (long) defaultValue);
            case FLOAT ->
                    value = Objects.requireNonNull(getPreferences()).getFloat(path, (float) defaultValue);
            case STRING ->
                    value = Objects.requireNonNull(getPreferences()).getString(path, (String) defaultValue);
            default -> throw new IllegalStateException(name());
        }
    }

    public void saveValue(@NonNull Object newValue) {
        Objects.requireNonNull(newValue);
        returnType.validate(newValue);
        switch (returnType) {
            case BOOLEAN -> saveBoolean(path, (boolean) newValue);
            case LONG -> saveLong(path, (long) newValue);
            case INTEGER -> saveInteger(path, (Integer) newValue);
            case FLOAT -> saveFloat(path, (float) newValue);
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

    public long getLong() {
        return (Long) value;
    }

    public float getFloat() {
        return (Float) value;
    }

    // Begin import / export

    public String getString() {
        return (String) value;
    }

    /**
     * @return the value of this setting as as generic object type.
     */
    @NonNull
    public Object getObjectValue() {
        return value;
    }

    /**
     * @return if the currently set value is the same as {@link #defaultValue}
     */
    public boolean isSetToDefault() {
        return value.equals(defaultValue);
    }

    /**
     * This could be yet another field,
     * for now use a simple switch statement since this method is not used outside this class.
     */
    private boolean includeWithImportExport() {
        return switch (this) { // Not useful to export, no reason to include it.
            case RYD_USER_ID, SB_LAST_VIP_CHECK, SETTINGS_INITIALIZED -> false;
            default -> true;
        };
    }

    /**
     * The path, minus any 'revanced' prefix to keep json concise.
     */
    private String getImportExportKey() {
        if (path.startsWith(OPTIONAL_REVANCED_SETTINGS_PREFIX)) {
            return path.substring(OPTIONAL_REVANCED_SETTINGS_PREFIX.length());
        }
        return path;
    }

    // End import / export

    public enum ReturnType {
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT,
        STRING;

        public void validate(@Nullable Object obj) throws IllegalArgumentException {
            if (!matches(obj)) {
                throw new IllegalArgumentException("'" + obj + "' does not match:" + this);
            }
        }

        public boolean matches(@Nullable Object obj) {
            return switch (this) {
                case BOOLEAN -> obj instanceof Boolean;
                case INTEGER -> obj instanceof Integer;
                case LONG -> obj instanceof Long;
                case FLOAT -> obj instanceof Float;
                case STRING -> obj instanceof String;
            };
        }
    }
}
