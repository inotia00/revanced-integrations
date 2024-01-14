package app.revanced.reddit.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.reddit.settings.SettingsEnum.ReturnType.BOOLEAN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum SettingsEnum {
    DEBUG_LOGGING("revanced_debug_logging", BOOLEAN, FALSE), // must be first value, otherwise logging during loading will not work

    HIDE_COMMENT_ADS("revanced_hide_comment_ads", BOOLEAN, TRUE, true,
            "Hide comment ads",
            "Hides comment ads."),
    HIDE_OLD_POST_ADS("revanced_hide_old_post_ads", BOOLEAN, TRUE, true,
            "Hide post ads",
            "Hides post ads. (Old method)"),
    HIDE_NEW_POST_ADS("revanced_hide_new_post_ads", BOOLEAN, TRUE, true,
            "Hide post ads",
            "Hides post ads. (New method)"),

    DISABLE_SCREENSHOT_POPUP("revanced_disable_screenshot_popup", BOOLEAN, TRUE,
            "Disable screenshot popup",
            "Disables the popup that shows up when taking a screenshot."),
    HIDE_CHAT_BUTTON("revanced_hide_chat_button", BOOLEAN, FALSE, true,
            "Hide chat button",
            "Hides the chat button in the navigation bar."),
    HIDE_CREATE_BUTTON("revanced_hide_create_button", BOOLEAN, FALSE, true,
            "Hide create button",
            "Hides the create button in the navigation bar."),
    HIDE_DISCOVER_BUTTON("revanced_hide_discover_button", BOOLEAN, FALSE, true,
            "Hide discover / community button",
            "Hides the discover or communities button in the navigation bar."),
    HIDE_RECENTLY_VISITED_SHELF("revanced_hide_recently_visited_shelf", BOOLEAN, FALSE,
            "Hide recently visited shelf",
            "Hides the recently visited shelf in the sidebar."),
    HIDE_TOOLBAR_BUTTON("revanced_hide_toolbar_button", BOOLEAN, FALSE, true,
            "Hide toolbar button",
            "Hides the r/place or Reddit recap button in the toolbar."),
    OPEN_LINKS_DIRECTLY("revanced_open_links_directly", BOOLEAN, TRUE,
            "Open links directly",
            "Skips over redirection URLs in external links."),
    OPEN_LINKS_EXTERNALLY("revanced_open_links_externally", BOOLEAN, TRUE,
            "Open links externally",
            "Opens links in your browser instead of in the in-app-browser."),
    SANITIZE_URL_QUERY("revanced_sanitize_url_query", BOOLEAN, TRUE,
            "Sanitize sharing links",
            "Removes tracking query parameters from URLs when sharing links.");

    private static final Map<String, SettingsEnum> pathToSetting = new HashMap<>(2 * values().length);

    static {
        loadAllSettings();

        for (SettingsEnum setting : values()) {
            pathToSetting.put(setting.path, setting);
        }
    }

    @NonNull
    public final String path;
    @NonNull
    public final Object defaultValue;
    @NonNull
    public final SharedPrefCategory sharedPref;
    @NonNull
    public final ReturnType returnType;
    /**
     * If the app should be rebooted, if this setting is changed
     */
    public final boolean rebootApp;
    @NonNull
    public final String title;
    @NonNull
    public final String summary;
    // Must be volatile, as some settings are read/write from different threads.
    // Of note, the object value is persistently stored using SharedPreferences (which is thread safe).
    @NonNull
    private Object value;

    SettingsEnum(String path, ReturnType returnType, Object defaultValue) {
        this(path, returnType, defaultValue, SharedPrefCategory.REDDIT, false, "", "");
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, String title, String summary) {
        this(path, returnType, defaultValue, SharedPrefCategory.REDDIT, false, title, summary);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, boolean rebootApp, String title, String summary) {
        this(path, returnType, defaultValue, SharedPrefCategory.REDDIT, rebootApp, title, summary);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefCategory prefName, boolean rebootApp, @NonNull String title, @NonNull String summary) {
        this.path = Objects.requireNonNull(path);
        this.returnType = Objects.requireNonNull(returnType);
        this.value = this.defaultValue = Objects.requireNonNull(defaultValue);
        this.sharedPref = Objects.requireNonNull(prefName);
        this.rebootApp = rebootApp;
        this.title = title;
        this.summary = summary;
    }

    @Nullable
    public static SettingsEnum settingFromPath(@NonNull String str) {
        return pathToSetting.get(str);
    }

    private static void loadAllSettings() {
        for (SettingsEnum setting : values()) {
            setting.load();
        }
    }

    private void load() {
        switch (returnType) {
            case BOOLEAN -> value = sharedPref.getBoolean(path, (boolean) defaultValue);
            case INTEGER -> value = sharedPref.getIntegerString(path, (Integer) defaultValue);
            case LONG -> value = sharedPref.getLongString(path, (Long) defaultValue);
            case FLOAT -> value = sharedPref.getFloatString(path, (Float) defaultValue);
            case STRING -> value = sharedPref.getString(path, (String) defaultValue);
            default -> throw new IllegalStateException(name());
        }
    }

    /**
     * Sets the value, and persistently saves it.
     */
    public void saveValue(@NonNull Object newValue) {
        returnType.validate(newValue);
        value = newValue; // Must set before saving to preferences (otherwise importing fails to update UI correctly).
        switch (returnType) {
            case BOOLEAN -> sharedPref.saveBoolean(path, (boolean) newValue);
            case INTEGER -> sharedPref.saveIntegerString(path, (Integer) newValue);
            case LONG -> sharedPref.saveLongString(path, (Long) newValue);
            case FLOAT -> sharedPref.saveFloatString(path, (Float) newValue);
            case STRING -> sharedPref.saveString(path, (String) newValue);
            default -> throw new IllegalStateException(name());
        }
    }

    public boolean getBoolean() {
        return (Boolean) value;
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

    @NonNull
    public String getString() {
        return (String) value;
    }

    @NonNull
    public String getSummary() {
        return summary;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * @return the value of this setting as as generic object type.
     */
    @NonNull
    public Object getObjectValue() {
        return value;
    }

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
