package app.revanced.integrations.settings;

import android.content.Context;
import android.util.Log;

import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.utils.SharedPrefHelper;

public enum SettingsEnum {

    // Ad settings
    HIDE_VIDEO_ADS("revanced_video_ads_removal", true, ReturnType.BOOLEAN, true),
    ADREMOVER_GENERAL_ADS("revanced_adremover_general_ads", true, ReturnType.BOOLEAN),
    ADREMOVER_BUTTON_ADS("revanced_adremover_button_ads", true, ReturnType.BOOLEAN),
    ADREMOVER_MERCHANDISE("revanced_adremover_merchandise", true, ReturnType.BOOLEAN),
    ADREMOVER_COMMUNITY_POSTS("revanced_adremover_community_posts", false, ReturnType.BOOLEAN),
    ADREMOVER_COMPACT_BANNER("revanced_adremover_compact_banner", true, ReturnType.BOOLEAN),
    ADREMOVER_MOVIE_SHELF("revanced_adremover_movie_shelf", false, ReturnType.BOOLEAN),
    ADREMOVER_MOVIE_REMOVAL("revanced_adremover_movie", false, ReturnType.BOOLEAN),
    ADREMOVER_CHANNEL_MEMBER_SHELF_REMOVAL("revanced_adremover_channel_member_shelf_removal", true, ReturnType.BOOLEAN),
    ADREMOVER_CHAPTER_TEASER_REMOVAL("revanced_adremover_chapter_teaser", true, ReturnType.BOOLEAN),	
    ADREMOVER_FEED_SURVEY("revanced_adremover_feed_survey", true, ReturnType.BOOLEAN),
    ADREMOVER_IMAGE_SHELF("revanced_adremover_image_shelf", true, ReturnType.BOOLEAN),
    ADREMOVER_COMMUNITY_GUIDELINES("revanced_adremover_community_guidelines", true, ReturnType.BOOLEAN),
    ADREMOVER_SUBSCRIBERS_COMMUNITY_GUIDELINES("revanced_adremover_subscribers_community_guidelines", true, ReturnType.BOOLEAN),
    ADREMOVER_EMERGENCY_BOX("revanced_adremover_emergency_box", true, ReturnType.BOOLEAN),
    ADREMOVER_INFO_PANEL("revanced_adremover_info_panel", true, ReturnType.BOOLEAN),
    ADREMOVER_MEDICAL_PANEL("revanced_adremover_medical_panel", true, ReturnType.BOOLEAN),
    ADREMOVER_PAID_CONTENT("revanced_adremover_paid_content", true, ReturnType.BOOLEAN, true),
    ADREMOVER_SUGGESTIONS("revanced_adremover_suggestions", true, ReturnType.BOOLEAN),
    ADREMOVER_LATEST_POSTS("revanced_adremover_latest_posts", true, ReturnType.BOOLEAN),
    ADREMOVER_CHANNEL_GUIDELINES("revanced_adremover_channel_guidelines", true, ReturnType.BOOLEAN),
    ADREMOVER_SELF_SPONSOR("revanced_adremover_self_sponsor", true, ReturnType.BOOLEAN),
    ADREMOVER_CHANNELBAR_JOIN_BUTTON("revanced_adremover_channelbar_join_button", true, ReturnType.BOOLEAN),
    ADREMOVER_CHAPTER_TEASER("revanced_adremover_chapter_teaser", true, ReturnType.BOOLEAN),
    ADREMOVER_GRAY_SEPARATOR("revanced_adremover_separator", true, ReturnType.BOOLEAN),
    ADREMOVER_OFFICIAL_CARDS("revanced_adremover_official_card_in_search", true, ReturnType.BOOLEAN, true),
    ADREMOVER_ALBUM_CARDS("revanced_adremover_album_card_in_search", true, ReturnType.BOOLEAN),
    ADREMOVER_BREAKING_NEWS_SHELF("revanced_adremover_breaking_news_shelf", false, ReturnType.BOOLEAN, true),
    ADREMOVER_WEB_SEARCH_PANEL("revanced_adremover_web_search_panel", true, ReturnType.BOOLEAN),
    ADREMOVER_VIEW_PRODUCTS("revanced_adremover_view_products", true, ReturnType.BOOLEAN),
    ADREMOVER_TIMED_REACTIONS("revanced_adremover_timed_reactions", false, ReturnType.BOOLEAN),
    ADREMOVER_USER_FILTER("revanced_adremover_user_filter", false, ReturnType.BOOLEAN),
    ADREMOVER_CUSTOM_FILTER("revanced_adremover_custom_strings", "", ReturnType.STRING, true),

    // Swipe controls
    ENABLE_SWIPE_AUTO_BRIGHTNESS("revanced_enable_swipe_auto_brightness", false, ReturnType.BOOLEAN),
    ENABLE_SWIPE_BRIGHTNESS("revanced_enable_swipe_brightness", true, ReturnType.BOOLEAN),
    ENABLE_SWIPE_VOLUME("revanced_enable_swipe_volume", true, ReturnType.BOOLEAN),
    ENABLE_PRESS_TO_SWIPE("revanced_enable_press_to_swipe", false, ReturnType.BOOLEAN),
    ENABLE_SWIPE_HAPTIC_FEEDBACK("revanced_enable_swipe_haptic_feedback", true, ReturnType.BOOLEAN),
    SWIPE_OVERLAY_TIMEOUT("revanced_swipe_overlay_timeout", 500L, ReturnType.LONG),
    SWIPE_OVERLAY_TEXT_SIZE("revanced_swipe_overlay_text_size", 27f, ReturnType.FLOAT),
    SWIPE_OVERLAY_BACKGROUND_ALPHA("revanced_swipe_overlay_background_alpha", 127, ReturnType.INTEGER),
    SWIPE_MAGNITUDE_THRESHOLD("revanced_swipe_magnitude_threshold", 0f, ReturnType.FLOAT),

    //Experimental Flags
    ENABLE_ALWAYS_SWIPE("revanced_enable_always_swipe", false, ReturnType.BOOLEAN, true, "revanced_reboot_warning_swipe"),

    // Layout settings (General)
    HIDE_STORIES_SHELF("revanced_hide_stories_shelf", true, ReturnType.BOOLEAN, true),
    HIDE_CREATE_BUTTON("revanced_hide_create_button", true, ReturnType.BOOLEAN, true),
    HIDE_SHORTS_BUTTON("revanced_hide_shorts_button", true, ReturnType.BOOLEAN, true),
    HIDE_SHORTS_SHELF("revanced_hide_shorts_shelf", true, ReturnType.BOOLEAN),
    HIDE_STARTUP_SHORTS_PLAYER("revanced_hide_startup_shorts_player", true, ReturnType.BOOLEAN),
    HIDE_SHORTS_PLAYER_COMMENTS_BUTTON("revanced_hide_shorts_player_comments_button", false, ReturnType.BOOLEAN),
    HIDE_SHORTS_PLAYER_REMIX_BUTTON("revanced_hide_shorts_player_remix_button", false, ReturnType.BOOLEAN),
    HIDE_SHORTS_PLAYER_THANKS_BUTTON("revanced_hide_shorts_player_thanks_button", true, ReturnType.BOOLEAN),
    HIDE_SHORTS_PLAYER_PLAYER_SUBSCRIPTIONS_BUTTON("revanced_hide_shorts_player_subscriptions_button", true, ReturnType.BOOLEAN),
    ENABLE_WIDE_SEARCHBAR("revanced_enable_wide_searchbar", false, ReturnType.BOOLEAN, true),
    ENABLE_TABLET_MINIPLAYER("revanced_enable_tablet_miniplayer", false, ReturnType.BOOLEAN, true),
    HIDE_AUTO_CAPTIONS("revanced_hide_auto_captions", false, ReturnType.BOOLEAN, true),
    HIDE_AUTO_PLAYER_POPUP_PANELS("revanced_hide_auto_player_popup_panels", true, ReturnType.BOOLEAN, true),
    HIDE_MIX_PLAYLISTS("revanced_hide_mix_playlists", false, ReturnType.BOOLEAN, true),
    HIDE_CROWDFUNDING_BOX("revanced_hide_crowdfunding_box", true, ReturnType.BOOLEAN, true),
    HIDE_EMAIL_ADDRESS("revanced_hide_email_address", true, ReturnType.BOOLEAN, true),
    ENABLE_PREMIUM_HEADER("revanced_override_premium_header", false, ReturnType.BOOLEAN, true),

    // Layout settings (Player)
    HIDE_CAST_BUTTON("revanced_hide_cast_button", true, ReturnType.BOOLEAN, true),
    HIDE_AUTOPLAY_BUTTON("revanced_hide_autoplay_button", true, ReturnType.BOOLEAN, true),
    HIDE_CAPTIONS_BUTTON("revanced_hide_captions_button", false, ReturnType.BOOLEAN),
    HIDE_ENDSCREEN_CARDS("revanced_hide_endscreen_cards", true, ReturnType.BOOLEAN, true),
    HIDE_INFO_CARDS("revanced_hide_info_cards", true, ReturnType.BOOLEAN, true),
    HIDE_CHANNEL_WATERMARK("revanced_hide_channel_watermark", true, ReturnType.BOOLEAN),
    HIDE_SUGGESTED_ACTION("revanced_hide_suggested_actions", true, ReturnType.BOOLEAN, true),
    HIDE_PALYER_OVERLAY_FILTER("revanced_hide_player_overlay_filter", false, ReturnType.BOOLEAN, true),

    // Layout settings (Fullscreen)
    HIDE_FULLSCREEN_BUTTON_CONTAINER("revanced_hide_fullscreen_button_container", false, ReturnType.BOOLEAN, true),
    HIDE_ENDSCREEN_OVERLAY("revanced_hide_endscreen_overlay", false, ReturnType.BOOLEAN, true),
    HIDE_FILMSTRIP_OVERLAY("revanced_hide_filmstrip_overlay", false, ReturnType.BOOLEAN, true),
    DISABLE_HAPTIC_FEEDBACK_SEEK("revanced_disable_haptic_feedback_seek", false, ReturnType.BOOLEAN),
    DISABLE_HAPTIC_FEEDBACK_SCRUBBING("revanced_disable_haptic_feedback_scrubbing", false, ReturnType.BOOLEAN),
    DISABLE_HAPTIC_FEEDBACK_CHAPTERS("revanced_disable_haptic_feedback_chapters", false, ReturnType.BOOLEAN),
    DISABLE_HAPTIC_FEEDBACK_ZOOM("revanced_disable_haptic_feedback_zoom", false, ReturnType.BOOLEAN),

    // Layout settings (Buttom Player)
    HIDE_LIKE_BUTTON("revanced_hide_button_like", false, ReturnType.BOOLEAN),
    HIDE_DISLIKE_BUTTON("revanced_hide_button_dislike", false, ReturnType.BOOLEAN),
    HIDE_SHARE_BUTTON("revanced_hide_button_share", false, ReturnType.BOOLEAN),
    HIDE_LIVE_CHAT_BUTTON("revanced_hide_button_live_chat", false, ReturnType.BOOLEAN),
    HIDE_REPORT_BUTTON("revanced_hide_button_report", false, ReturnType.BOOLEAN),
    HIDE_CREATE_SHORT_BUTTON("revanced_hide_button_create_short", false, ReturnType.BOOLEAN),
    HIDE_THANKS_BUTTON("revanced_hide_button_thanks", false, ReturnType.BOOLEAN),
    HIDE_CREATE_CLIP_BUTTON("revanced_hide_button_create_clip", false, ReturnType.BOOLEAN),
    HIDE_DOWNLOAD_BUTTON("revanced_hide_button_download", false, ReturnType.BOOLEAN),
    HIDE_PLAYLIST_BUTTON("revanced_hide_button_playlist", false, ReturnType.BOOLEAN),
    HIDE_COMMENTS_SECTION("revanced_hide_comments_section", false, ReturnType.BOOLEAN),
    HIDE_PREVIEW_COMMENT("revanced_hide_preview_comment", false, ReturnType.BOOLEAN),

    // Layout settings (Flyout Panel)
    ENABLE_OLD_QUALITY_LAYOUT("revanced_enable_old_quality_layout", true, ReturnType.BOOLEAN),
    HIDE_CAPTIONS_MENU("revanced_hide_menu_captions", false, ReturnType.BOOLEAN),
    HIDE_LOOP_MENU("revanced_hide_menu_loop_video", false, ReturnType.BOOLEAN),
    HIDE_AMBIENT_MENU("revanced_hide_menu_ambient_mode", false, ReturnType.BOOLEAN),
    HIDE_REPORT_MENU("revanced_hide_menu_report", false, ReturnType.BOOLEAN),
    HIDE_HELP_MENU("revanced_hide_menu_help", false, ReturnType.BOOLEAN),
    HIDE_MORE_MENU("revanced_hide_menu_more_info", false, ReturnType.BOOLEAN),
    HIDE_LISTENING_CONTROLS_MENU("revanced_hide_menu_listening_controls", false, ReturnType.BOOLEAN),
    HIDE_AUDIO_TRACK_MENU("revanced_hide_menu_audio_track", false, ReturnType.BOOLEAN),
    HIDE_WATCH_IN_VR_MENU("revanced_hide_menu_watch_in_vr", false, ReturnType.BOOLEAN),
    HIDE_NERDS_MENU("hide_menu_stats_for_nerds", false, ReturnType.BOOLEAN),
    HIDE_YT_MUSIC_MENU("revanced_hide_menu_listen_with_youtube_music", false, ReturnType.BOOLEAN),

    // Layout settings (Seekbar)
    ENABLE_OLD_SEEKBAR_COLOR("revanced_enable_old_seekbar_color", true, ReturnType.BOOLEAN, true),
    ENABLE_SEEKBAR_TAPPING("revanced_enable_seekbar_tapping", true, ReturnType.BOOLEAN),
    HIDE_TIME_AND_SEEKBAR("revanced_hide_time_and_seekbar", false, ReturnType.BOOLEAN),

    //Misc Settings
    ENABLE_EXTERNAL_BROWSER("revanced_enable_external_browser", true, ReturnType.BOOLEAN, true),
    ENABLE_OPEN_LINKS_DIRECTLY("revanced_enable_open_links_directly", true, ReturnType.BOOLEAN),
    ENABLE_MINIMIZED_PLAYBACK("revanced_enable_minimized_playback", true, ReturnType.BOOLEAN),
    ENABLE_HDR_AUTO_BRIGHTNESS("revanced_enable_hdr_auto_brightness", true, ReturnType.BOOLEAN),
    CUSTOM_VIDEO_BUFFER_MAXIMUM("revanced_custom_video_buffer_maximum", 120000, ReturnType.INTEGER),
    CUSTOM_VIDEO_BUFFER_PLAYBACK_START("revanced_custom_video_buffer_playback_start", 2500, ReturnType.INTEGER),
    CUSTOM_VIDEO_BUFFER_REBUFFER("revanced_custom_video_buffer_rebuffer", 5000, ReturnType.INTEGER),

    //Video Settings
    DEFAULT_VIDEO_SPEED("revanced_default_video_speed", -2.0f, ReturnType.FLOAT),
    DEFAULT_VIDEO_QUALITY_WIFI("revanced_default_video_quality_wifi", -2, ReturnType.INTEGER),
    DEFAULT_VIDEO_QUALITY_MOBILE("revanced_default_video_quality_mobile", -2, ReturnType.INTEGER),
    ENABLE_CUSTOM_VIDEO_SPEED("revanced_enable_custom_video_speed", false, ReturnType.BOOLEAN, true),
    ENABLE_SAVE_VIDEO_QUALITY("revanced_enable_save_video_quality", false, ReturnType.BOOLEAN),

    //Overlaybutton Settings
    OVERLAY_BUTTON_WHITELIST("revanced_overlay_button_whitelist", false, ReturnType.BOOLEAN),
    OVERLAY_BUTTON_COPY("revanced_overlay_button_copy_url", false, ReturnType.BOOLEAN),
    OVERLAY_BUTTON_COPY_WITH_TIMESTAMP("revanced_overlay_button_copy_url_with_timestamp", false, ReturnType.BOOLEAN),
    OVERLAY_BUTTON_AUTO_REPEAT("revanced_overlay_button_auto_repeat", false, ReturnType.BOOLEAN),
    ENABLE_ALWAYS_AUTO_REPEAT("revanced_enable_always_auto_repeat", false, ReturnType.BOOLEAN),
    //Download Settings
    OVERLAY_BUTTON_DOWNLOADS("revanced_overlay_button_downloads", true, ReturnType.BOOLEAN),
    DOWNLOADER_PACKAGE_NAME("revanced_downloader_package_name", null, ReturnType.STRING),
    // Whitelist Settings
    ADS_WHITELIST("revanced_whitelist_ads", false, ReturnType.BOOLEAN),
    SPEED_WHITELIST("revanced_whitelist_speed", false, ReturnType.BOOLEAN),
    SB_WHITELIST("revanced_whitelisting_sponsorblock", false, ReturnType.BOOLEAN),

    //Experimental Flags
    FIX_VIDEO_PLAYBACK("revanced_fix_video_playback", false, ReturnType.BOOLEAN, true, "revanced_reboot_warning_playback"),
    ENABLE_OLD_LAYOUT("revanced_enable_old_layout", false, ReturnType.BOOLEAN, true, "revanced_reboot_warning_oldlayout"),
    ENABLE_TABLET_LAYOUT("revanced_enable_tablet_layout", false, ReturnType.BOOLEAN, true, "revanced_reboot_warning_tablet"),
    ENABLE_PHONE_LAYOUT("revanced_enable_phone_layout", false, ReturnType.BOOLEAN, true, "revanced_reboot_warning_phone"),

    //RYD Settings
    RYD_USER_ID("ryd_userId", null, SharedPrefHelper.SharedPrefNames.RYD, ReturnType.STRING),
    RYD_ENABLED("ryd_enabled", true, SharedPrefHelper.SharedPrefNames.RYD, ReturnType.BOOLEAN),
    RYD_MIRROR_ENABLED("ryd_mirror_enabled", false, SharedPrefHelper.SharedPrefNames.RYD, ReturnType.BOOLEAN),
    RYD_SHOW_DISLIKE_PERCENTAGE("ryd_show_dislike_percentage", false, SharedPrefHelper.SharedPrefNames.RYD, ReturnType.BOOLEAN),

    //SponsorBlock Settings
    SB_ENABLED("sb-enabled", true, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_MIRROR_ENABLED("sb-mirror-enabled", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_SHOW_TOAST_WHEN_SKIP("show-toast", true, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_COUNT_SKIPS("count-skips", true, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_UUID("uuid", null, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.STRING),
    SB_ADJUST_NEW_SEGMENT_STEP("new-segment-step-accuracy", 150, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.INTEGER),
    SB_MIN_DURATION("sb-min-duration", 0F, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.FLOAT),
    SB_SEEN_GUIDELINES("sb-seen-gl", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_NEW_SEGMENT_ENABLED("sb-new-segment-enabled", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_VOTING_ENABLED("sb-voting-enabled", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_SKIPPED_SEGMENTS("sb-skipped-segments", 0, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.INTEGER),
    SB_SKIPPED_SEGMENTS_TIME("sb-skipped-segments-time", 0L, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.LONG),
    SB_SHOW_TIME_WITHOUT_SEGMENTS("sb-length-without-segments", true, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_IS_VIP("sb-is-vip", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN),
    SB_LAST_VIP_CHECK("sb-last-vip-check", 0L, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.LONG),
    SB_API_URL("sb-api-url", "https://sponsor.ajay.app/api/", SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.STRING),
    SB_API_MIRROR_URL("sb-api-mirror-url", "https://sponsorblock.kavin.rocks/api/", SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.STRING),
    SB_FIRSTRUN("sb-firstrun", false, SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK, ReturnType.BOOLEAN);

    private final String path;
    private final Object defaultValue;
    private final SharedPrefHelper.SharedPrefNames sharedPref;
    private final ReturnType returnType;
    private final boolean rebootApp;
    private final String rebootApp_Warning;

    private Object value = null;

    SettingsEnum(String path, Object defaultValue, ReturnType returnType) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.sharedPref = SharedPrefHelper.SharedPrefNames.REVANCED;
        this.returnType = returnType;
        this.rebootApp = false;
        this.rebootApp_Warning = "";
    }

    SettingsEnum(String path, Object defaultValue, SharedPrefHelper.SharedPrefNames prefName, ReturnType returnType) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.sharedPref = prefName;
        this.returnType = returnType;
        this.rebootApp = false;
        this.rebootApp_Warning = "";
    }

    SettingsEnum(String path, Object defaultValue, ReturnType returnType, Boolean rebootApp) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.sharedPref = SharedPrefHelper.SharedPrefNames.REVANCED;
        this.returnType = returnType;
        this.rebootApp = rebootApp;
        this.rebootApp_Warning = "";
    }

    SettingsEnum(String path, Object defaultValue, ReturnType returnType, Boolean rebootApp, String rebootApp_Warning) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.sharedPref = SharedPrefHelper.SharedPrefNames.REVANCED;
        this.returnType = returnType;
        this.rebootApp = rebootApp;
        this.rebootApp_Warning = rebootApp_Warning;
    }

    static {
        load();
    }

    private static void load() {
        Context context = ReVancedUtils.getContext();
        if (context == null) {
            Log.e("revanced: SettingsEnum", "Context returned null! Setings NOT initialized");
        } else {
            try {
                for (SettingsEnum setting : values()) {
                    Object value = setting.getDefaultValue();

                    //LogHelper is not initialized here
                    Log.d("revanced: SettingsEnum", "Loading Setting: " + setting.name());

                    switch (setting.getReturnType()) {
                        case FLOAT:
                            value = SharedPrefHelper.getFloat(context, setting.sharedPref, setting.getPath(), (float) setting.getDefaultValue());
                            break;
                        case LONG:
                            value = SharedPrefHelper.getLong(context, setting.sharedPref, setting.getPath(), (long) setting.getDefaultValue());
                            break;
                        case BOOLEAN:
                            value = SharedPrefHelper.getBoolean(context, setting.sharedPref, setting.getPath(), (boolean) setting.getDefaultValue());
                            break;
                        case INTEGER:
                            value = SharedPrefHelper.getInt(context, setting.sharedPref, setting.getPath(), (int) setting.getDefaultValue());
                            break;
                        case STRING:
                            value = SharedPrefHelper.getString(context, setting.sharedPref, setting.getPath(), (String) setting.getDefaultValue());
                            break;
                        default:
                            LogHelper.printException(SettingsEnum.class, "Setting does not have a valid Type. Name is: " + setting.name());
                            break;
                    }
                    setting.setValue(value);

                    //LogHelper is not initialized here
                    Log.d("revanced: SettingsEnum", "Loaded Setting: " + setting.name() + " Value: " + value);
                }
            } catch (Throwable th) {
                LogHelper.printException(SettingsEnum.class, "Error during load()!", th);
            }
        }
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }

    public void saveValue(Object newValue) {
        Context context = ReVancedUtils.getContext();
        if (context != null) {
            if (returnType == ReturnType.BOOLEAN) {
                SharedPrefHelper.saveBoolean(context, sharedPref, path, (Boolean) newValue);
            } else {
                SharedPrefHelper.saveString(context, sharedPref, path, newValue + "");
            }
            value = newValue;
        } else {
            LogHelper.printException(SettingsEnum.class, "Context on SaveValue is null!");
        }
    }

    public int getInt() {
        return (int) value;
    }

    public String getString() {
        return (String) value;
    }

    public boolean getBoolean() {
        return (Boolean) value;
    }

    public Long getLong() {
        return (Long) value;
    }

    public Float getFloat() {
        return (Float) value;
    }

    public boolean isNull() { return value == null; }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getPath() {
        return path;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public boolean shouldRebootOnChange() {
        return rebootApp;
    }

    public String shouldWarningOnChange() {
        return rebootApp_Warning;
    }

}
