package app.revanced.integrations.shared.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Settings shared across multiple apps.
 * <p>
 * To ensure this class is loaded when the UI is created, app specific setting bundles should extend
 * or reference this class.
 */
public class BaseSettings {
    public static final BooleanSetting ENABLE_DEBUG_LOGGING = new BooleanSetting("revanced_enable_debug_logging", FALSE);
    /**
     * When enabled, share the debug logs with care.
     * The buffer contains select user data, including the client ip address and information that could identify the end user.
     */
    public static final BooleanSetting ENABLE_DEBUG_BUFFER_LOGGING = new BooleanSetting("revanced_enable_debug_buffer_logging", FALSE);
    public static final BooleanSetting SETTINGS_INITIALIZED = new BooleanSetting("revanced_settings_initialized", FALSE, false, false);

    /**
     * These settings are used by YouTube and YouTube Music.
     */
    public static final BooleanSetting HIDE_FULLSCREEN_ADS = new BooleanSetting("revanced_hide_fullscreen_ads", TRUE, true);
    public static final BooleanSetting HIDE_PROMOTION_ALERT_BANNER = new BooleanSetting("revanced_hide_promotion_alert_banner", TRUE);

    public static final BooleanSetting HIDE_SETTINGS_MENU = new BooleanSetting("revanced_hide_settings_menu", FALSE);
    public static final StringSetting HIDE_SETTINGS_MENU_FILTER_STRINGS = new StringSetting("revanced_hide_settings_menu_filter_strings", "", true);

    public static final BooleanSetting DISABLE_AUTO_CAPTIONS = new BooleanSetting("revanced_disable_auto_captions", FALSE, true);

    public static final BooleanSetting BYPASS_IMAGE_REGION_RESTRICTIONS = new BooleanSetting("revanced_bypass_image_region_restrictions", FALSE, true);

    /**
     * @noinspection DeprecatedIsStillUsed
     */
    @Deprecated
    // The official ReVanced does not offer this, so it has been removed from the settings only. Users can still access settings through import / export settings.
    public static final StringSetting BYPASS_IMAGE_REGION_RESTRICTIONS_DOMAIN = new StringSetting("revanced_bypass_image_region_restrictions_domain", "yt4.ggpht.com", true);

    public static final BooleanSetting SANITIZE_SHARING_LINKS = new BooleanSetting("revanced_sanitize_sharing_links", TRUE, true);
}
