package app.revanced.integrations.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.integrations.settings.SettingsEnum.ReturnType.BOOLEAN;
import static app.revanced.integrations.settings.SettingsEnum.ReturnType.FLOAT;
import static app.revanced.integrations.settings.SettingsEnum.ReturnType.INTEGER;
import static app.revanced.integrations.settings.SettingsEnum.ReturnType.LONG;
import static app.revanced.integrations.settings.SettingsEnum.ReturnType.STRING;
import static app.revanced.integrations.utils.SharedPrefHelper.SharedPrefNames.REVANCED;
import static app.revanced.integrations.utils.SharedPrefHelper.SharedPrefNames.RYD;
import static app.revanced.integrations.utils.SharedPrefHelper.SharedPrefNames.SPONSOR_BLOCK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import app.revanced.integrations.utils.SharedPrefHelper;
import app.revanced.integrations.utils.StringRef;


public enum SettingsEnum {

    // Ads
    HIDE_GENERAL_ADS("revanced_hide_general_ads", BOOLEAN, TRUE),
    HIDE_GET_PREMIUM("revanced_hide_get_premium", BOOLEAN, TRUE, true),
    HIDE_IMAGE_SHELF("revanced_hide_image_shelf", BOOLEAN, TRUE),
    HIDE_MERCHANDISE_SHELF("revanced_hide_merchandise_shelf", BOOLEAN, TRUE),
    HIDE_PAID_PROMOTION("revanced_hide_paid_promotion_banner", BOOLEAN, TRUE),
    HIDE_SELF_SPONSOR_CARDS("revanced_hide_self_sponsor_cards", BOOLEAN, TRUE),
    HIDE_VIDEO_ADS("revanced_hide_video_ads", BOOLEAN, TRUE, true),
    HIDE_VIEW_PRODUCTS("revanced_hide_view_products", BOOLEAN, TRUE),
    HIDE_WEB_SEARCH_RESULTS("revanced_hide_web_search_results", BOOLEAN, TRUE),


    // Bottom Player
    HIDE_CREATE_CLIP_BUTTON("revanced_hide_button_create_clip", BOOLEAN, FALSE),
    HIDE_DISLIKE_BUTTON("revanced_hide_button_dislike", BOOLEAN, FALSE),
    HIDE_DOWNLOAD_BUTTON("revanced_hide_button_download", BOOLEAN, FALSE),
    HIDE_LIKE_BUTTON("revanced_hide_button_like", BOOLEAN, FALSE),
    HIDE_LIVE_CHAT_BUTTON("revanced_hide_button_live_chat", BOOLEAN, FALSE),
    HIDE_PLAYLIST_BUTTON("revanced_hide_button_playlist", BOOLEAN, FALSE),
    HIDE_REWARDS_BUTTON("revanced_hide_button_rewards", BOOLEAN, FALSE),
    HIDE_REMIX_BUTTON("revanced_hide_button_remix", BOOLEAN, FALSE),
    HIDE_REPORT_BUTTON("revanced_hide_button_report", BOOLEAN, FALSE),
    HIDE_SHARE_BUTTON("revanced_hide_button_share", BOOLEAN, FALSE),
    HIDE_SHOP_BUTTON("revanced_hide_button_shop", BOOLEAN, FALSE),
    HIDE_THANKS_BUTTON("revanced_hide_button_thanks", BOOLEAN, FALSE),


    // Comments
    HIDE_CHANNEL_GUIDELINES("revanced_hide_channel_guidelines", BOOLEAN, TRUE),
    HIDE_COMMENTS_SECTION("revanced_hide_comments_section", BOOLEAN, FALSE),
    HIDE_COMMENTS_THANKS_BUTTON("revanced_hide_comments_thanks_button", BOOLEAN, FALSE),
    HIDE_EMOJI_PICKER("revanced_hide_emoji_picker", BOOLEAN, FALSE),
    HIDE_PREVIEW_COMMENT("revanced_hide_preview_comment", BOOLEAN, FALSE),


    // Flyout Panel

    // Feed Flyout Panel
    HIDE_FEED_FLYOUT_PANEL("revanced_hide_feed_flyout_panel", BOOLEAN, FALSE),
    HIDE_FEED_FLYOUT_PANEL_FILTER_STRINGS("revanced_hide_feed_flyout_panel_filter_strings", STRING, "", true,
            parents(HIDE_FEED_FLYOUT_PANEL)),

    // Player Flyout Panel
    ENABLE_OLD_QUALITY_LAYOUT("revanced_enable_old_quality_layout", BOOLEAN, TRUE),
    HIDE_PLAYER_FLYOUT_PANEL_AMBIENT("revanced_hide_player_flyout_panel_ambient_mode", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_AUDIO_TRACK("revanced_hide_player_flyout_panel_audio_track", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_CAPTIONS("revanced_hide_player_flyout_panel_captions", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_HELP("revanced_hide_player_flyout_panel_help", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_LISTENING_CONTROLS("revanced_hide_player_flyout_panel_listening_controls", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_LOCK_SCREEN("revanced_hide_player_flyout_panel_lock_screen", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_LOOP("revanced_hide_player_flyout_panel_loop_video", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_MORE("revanced_hide_player_flyout_panel_more_info", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_NERDS("revanced_hide_player_flyout_panel_stats_for_nerds", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_QUALITY("revanced_hide_player_flyout_panel_qualities", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_REPORT("revanced_hide_player_flyout_panel_report", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_SPEED("revanced_hide_player_flyout_panel_speed", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_WATCH_IN_VR("revanced_hide_player_flyout_panel_watch_in_vr", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_YT_MUSIC("revanced_hide_player_flyout_panel_listen_with_youtube_music", BOOLEAN, FALSE),


    // Fullscreen
    ENABLE_COMPACT_CONTROLS_OVERLAY("revanced_enable_compact_controls_overlay", BOOLEAN, FALSE, true),
    HIDE_AUTOPLAY_PREVIEW("revanced_hide_autoplay_preview", BOOLEAN, FALSE, true),
    HIDE_END_SCREEN_OVERLAY("revanced_hide_end_screen_overlay", BOOLEAN, FALSE, true),
    HIDE_FULLSCREEN_PANELS("revanced_hide_fullscreen_panels", BOOLEAN, FALSE, true),
    SHOW_FULLSCREEN_TITLE("revanced_show_fullscreen_title", BOOLEAN, TRUE, true,
            parents(HIDE_FULLSCREEN_PANELS)),

    // Quick Actions
    HIDE_QUICK_ACTIONS("revanced_hide_quick_actions", BOOLEAN, FALSE, true),
    HIDE_QUICK_ACTIONS_COMMENT_BUTTON("revanced_hide_quick_actions_comment", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_DISLIKE_BUTTON("revanced_hide_quick_actions_dislike", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_LIKE_BUTTON("revanced_hide_quick_actions_like", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_LIVE_CHAT_BUTTON("revanced_hide_quick_actions_live_chat", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_MORE_BUTTON("revanced_hide_quick_actions_more", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_PLAYLIST_BUTTON("revanced_hide_quick_actions_playlist", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_RELATED_VIDEO("revanced_hide_quick_actions_related_videos", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_SHARE_BUTTON("revanced_hide_quick_actions_share", BOOLEAN, FALSE),

    // Experimental Flags
    DISABLE_LANDSCAPE_MODE("revanced_disable_landscape_mode", BOOLEAN, FALSE, true),


    // General
    DISABLE_AUTO_CAPTIONS("revanced_disable_auto_captions", BOOLEAN, FALSE, true),
    ENABLE_PREMIUM_HEADER("revanced_override_premium_header", BOOLEAN, FALSE, true),
    ENABLE_TABLET_MINI_PLAYER("revanced_enable_tablet_mini_player", BOOLEAN, FALSE, true),
    ENABLE_WIDE_SEARCH_BAR("revanced_enable_wide_search_bar", BOOLEAN, FALSE, true),
    HIDE_ACCOUNT_MENU("revanced_hide_account_menu", BOOLEAN, FALSE),
    HIDE_ACCOUNT_MENU_FILTER_STRINGS("revanced_hide_account_menu_filter_strings", STRING, "YouTube Music\nYouTube Kids", true,
            parents(HIDE_ACCOUNT_MENU)),
    HIDE_AUTO_PLAYER_POPUP_PANELS("revanced_hide_auto_player_popup_panels", BOOLEAN, TRUE, true),
    HIDE_CATEGORY_BAR_IN_FEED("revanced_hide_category_bar_in_feed", BOOLEAN, FALSE, true),
    HIDE_CATEGORY_BAR_IN_RELATED_VIDEO("revanced_hide_category_bar_in_related_video", BOOLEAN, FALSE, true),
    HIDE_CATEGORY_BAR_IN_SEARCH_RESULTS("revanced_hide_category_bar_in_search_results", BOOLEAN, FALSE, true),
    HIDE_CHANNEL_LIST_SUBMENU("revanced_hide_channel_list_submenu", BOOLEAN, FALSE, true),
    HIDE_CROWDFUNDING_BOX("revanced_hide_crowdfunding_box", BOOLEAN, TRUE, true),
    HIDE_EMAIL_ADDRESS("revanced_hide_email_address", BOOLEAN, TRUE, true),
    HIDE_FLOATING_MICROPHONE("revanced_hide_floating_microphone", BOOLEAN, TRUE, true),
    HIDE_LATEST_VIDEOS_BUTTON("revanced_hide_latest_videos_button", BOOLEAN, TRUE),
    HIDE_LOAD_MORE_BUTTON("revanced_hide_load_more_button", BOOLEAN, TRUE, true),
    HIDE_MIX_PLAYLISTS("revanced_hide_mix_playlists", BOOLEAN, FALSE),
    HIDE_SNACK_BAR("revanced_hide_snack_bar", BOOLEAN, FALSE),
    HIDE_SUGGESTIONS_SHELF("revanced_hide_suggestions_shelf", BOOLEAN, FALSE, true),
    HIDE_TRENDING_SEARCHES("revanced_hide_trending_searches", BOOLEAN, TRUE),

    // Layout
    CUSTOM_FILTER("revanced_custom_filter", BOOLEAN, FALSE),
    CUSTOM_FILTER_STRINGS("revanced_custom_filter_strings", STRING, "", true,
            parents(CUSTOM_FILTER)),
    HIDE_ALBUM_CARDS("revanced_hide_album_card", BOOLEAN, TRUE),
    HIDE_BROWSE_STORE_BUTTON("revanced_hide_browse_store_button", BOOLEAN, TRUE),
    HIDE_CHANNEL_MEMBER_SHELF("revanced_hide_channel_member_shelf", BOOLEAN, TRUE),
    HIDE_CHIPS_SHELF("revanced_hide_chips_shelf", BOOLEAN, TRUE),
    HIDE_COMMUNITY_POSTS_HOME("revanced_hide_community_posts_home", BOOLEAN, TRUE),
    HIDE_COMMUNITY_POSTS_SUBSCRIPTIONS("revanced_hide_community_posts_subscriptions", BOOLEAN, FALSE),
    HIDE_EXPANDABLE_CHIP("revanced_hide_expandable_chip", BOOLEAN, TRUE),
    HIDE_FEED_SURVEY("revanced_hide_feed_survey", BOOLEAN, TRUE),
    HIDE_GRAY_DESCRIPTION("revanced_hide_gray_description", BOOLEAN, TRUE),
    HIDE_GRAY_SEPARATOR("revanced_hide_gray_separator", BOOLEAN, TRUE),
    HIDE_INFO_PANEL("revanced_hide_info_panel", BOOLEAN, TRUE),
    HIDE_JOIN_BUTTON("revanced_hide_join_button", BOOLEAN, TRUE),
    HIDE_LATEST_POSTS("revanced_hide_latest_posts", BOOLEAN, TRUE),
    HIDE_MEDICAL_PANEL("revanced_hide_medical_panel", BOOLEAN, TRUE),
    HIDE_MOVIE_SHELF("revanced_hide_movie_shelf", BOOLEAN, FALSE),
    HIDE_OFFICIAL_HEADER("revanced_hide_official_header", BOOLEAN, FALSE),
    HIDE_TICKET_SHELF("revanced_hide_ticket_shelf", BOOLEAN, TRUE),
    HIDE_TIMED_REACTIONS("revanced_hide_timed_reactions", BOOLEAN, FALSE),

    // Description
    HIDE_CHAPTERS("revanced_hide_chapters", BOOLEAN, FALSE),
    HIDE_INFO_CARDS_SECTION("revanced_hide_info_cards_section", BOOLEAN, FALSE),
    HIDE_GAME_SECTION("revanced_hide_game_section", BOOLEAN, FALSE),
    HIDE_MUSIC_SECTION("revanced_hide_music_section", BOOLEAN, FALSE),
    HIDE_PLACE_SECTION("revanced_hide_place_section", BOOLEAN, FALSE),
    HIDE_TRANSCIPT_SECTION("revanced_hide_transcript_section", BOOLEAN, FALSE),


    // Misc
    BYPASS_AMBIENT_MODE_RESTRICTIONS("revanced_bypass_ambient_mode_restrictions", BOOLEAN, FALSE),
    ENABLE_DEBUG_LOGGING("revanced_enable_debug_logging", BOOLEAN, FALSE),
    ENABLE_DEBUG_BUFFER_LOGGING("revanced_enable_debug_buffer_logging", BOOLEAN, FALSE),
    ENABLE_EXTERNAL_BROWSER("revanced_enable_external_browser", BOOLEAN, TRUE, true),
    ENABLE_NEW_SPLASH_ANIMATION("revanced_enable_new_splash_animation", BOOLEAN, FALSE, true),
    ENABLE_OPEN_LINKS_DIRECTLY("revanced_enable_open_links_directly", BOOLEAN, TRUE),
    DOUBLE_BACK_TIMEOUT("revanced_double_back_timeout", INTEGER, 2, true),

    // Experimental Flags
    DISABLE_QUIC_PROTOCOL("revanced_disable_quic_protocol", BOOLEAN, FALSE, true, "revanced_reboot_warning_quic"),
    ENABLE_OPUS_CODEC("revanced_enable_opus_codec", BOOLEAN, FALSE, true),
    ENABLE_PHONE_LAYOUT("revanced_enable_phone_layout", BOOLEAN, FALSE, true, "revanced_reboot_warning_phone"),
    ENABLE_TABLET_LAYOUT("revanced_enable_tablet_layout", BOOLEAN, FALSE, true, "revanced_reboot_warning_tablet"),
    ENABLE_VP9_CODEC("revanced_enable_vp9_codec", BOOLEAN, FALSE, true, "revanced_reboot_warning_vp9"),
    SPOOF_APP_VERSION("revanced_spoof_app_version", BOOLEAN, FALSE, true),
    SPOOF_APP_VERSION_TARGET("revanced_spoof_app_version_target", STRING, "18.20.39", true,
            parents(SPOOF_APP_VERSION)),
    SPOOF_PLAYER_PARAMETER("revanced_spoof_player_parameter", BOOLEAN, FALSE, true),


    // Navigation
    CHANGE_HOMEPAGE_TO_SUBSCRIPTION("revanced_change_homepage", BOOLEAN, FALSE, true),
    ENABLE_TABLET_NAVIGATION_BAR("revanced_enable_tablet_navigation_bar", BOOLEAN, FALSE, true),
    HIDE_CREATE_BUTTON("revanced_hide_create_button", BOOLEAN, TRUE, true),
    HIDE_HOME_BUTTON("revanced_hide_home_button", BOOLEAN, FALSE, true),
    HIDE_LIBRARY_BUTTON("revanced_hide_library_button", BOOLEAN, FALSE, true),
    HIDE_NAVIGATION_LABEL("revanced_hide_navigation_label", BOOLEAN, FALSE, true),
    HIDE_SHORTS_BUTTON("revanced_hide_shorts_button", BOOLEAN, FALSE, true),
    HIDE_SUBSCRIPTIONS_BUTTON("revanced_hide_subscriptions_button", BOOLEAN, FALSE, true),
    OPEN_LIBRARY_STARTUP("revanced_open_library_startup", BOOLEAN, FALSE, true),
    SWITCH_CREATE_NOTIFICATION("revanced_switching_create_notification", BOOLEAN, TRUE, true),


    // Overlay Button
    ALWAYS_REPEAT("revanced_always_repeat", BOOLEAN, FALSE),
    ALWAYS_REPEAT_PAUSE("revanced_always_repeat_pause", BOOLEAN, FALSE),
    OVERLAY_BUTTON_ALWAYS_REPEAT("revanced_overlay_button_always_repeat", BOOLEAN, TRUE),
    OVERLAY_BUTTON_COPY_VIDEO_URL("revanced_overlay_button_copy_video_url", BOOLEAN, TRUE),
    OVERLAY_BUTTON_COPY_VIDEO_URL_TIMESTAMP("revanced_overlay_button_copy_video_url_timestamp", BOOLEAN, FALSE),
    OVERLAY_BUTTON_EXTERNAL_DOWNLOADER("revanced_overlay_button_external_downloader", BOOLEAN, TRUE),
    OVERLAY_BUTTON_WHITELIST("revanced_overlay_button_whitelist", BOOLEAN, FALSE),
    WHITELIST_API_KEY("revanced_whitelist_api_key", STRING, "", true),
    OVERLAY_BUTTON_SPEED_DIALOG("revanced_overlay_button_speed_dialog", BOOLEAN, TRUE),
    EXTERNAL_DOWNLOADER_PACKAGE_NAME("revanced_external_downloader_package_name", STRING, "com.deniscerri.ytdl", true),

    // Channel Whitelist
    SPEED_WHITELIST("revanced_whitelist_speed", BOOLEAN, FALSE),
    SB_WHITELIST("revanced_whitelisting_sponsorblock", BOOLEAN, FALSE),

    // Experimental Flags
    HOOK_DOWNLOAD_BUTTON("revanced_hook_download_button", BOOLEAN, FALSE, true),


    // Player
    HIDE_AUDIO_TRACK_BUTTON("revanced_hide_audio_track_button", BOOLEAN, TRUE),
    HIDE_AUTOPLAY_BUTTON("revanced_hide_autoplay_button", BOOLEAN, TRUE, true),
    HIDE_CAPTIONS_BUTTON("revanced_hide_captions_button", BOOLEAN, FALSE),
    HIDE_CAST_BUTTON("revanced_hide_cast_button", BOOLEAN, TRUE, true),
    HIDE_CHANNEL_WATERMARK("revanced_hide_channel_watermark", BOOLEAN, TRUE),
    HIDE_COLLAPSE_BUTTON("revanced_hide_collapse_button", BOOLEAN, FALSE),
    HIDE_END_SCREEN_CARDS("revanced_hide_end_screen_cards", BOOLEAN, TRUE, true),
    HIDE_INFO_CARDS("revanced_hide_info_cards", BOOLEAN, TRUE, true),
    HIDE_PLAYER_BUTTON_BACKGROUND("revanced_hide_player_button_background", BOOLEAN, FALSE, true),
    HIDE_PLAYER_OVERLAY_FILTER("revanced_hide_player_overlay_filter", BOOLEAN, FALSE, true),
    HIDE_PREVIOUS_NEXT_BUTTON("revanced_hide_previous_next_button", BOOLEAN, FALSE),
    HIDE_SEEK_MESSAGE("revanced_hide_seek_message", BOOLEAN, FALSE, true),
    HIDE_SPEED_OVERLAY("revanced_hide_speed_overlay", BOOLEAN, FALSE, true),
    HIDE_SUGGESTED_ACTION("revanced_hide_suggested_actions", BOOLEAN, TRUE, true),
    HIDE_YOUTUBE_MUSIC_BUTTON("revanced_hide_youtube_music_button", BOOLEAN, FALSE),

    // Experimental Flags
    HIDE_FILMSTRIP_OVERLAY("revanced_hide_filmstrip_overlay", BOOLEAN, FALSE, true),
    HIDE_SUGGESTED_VIDEO_OVERLAY("revanced_hide_suggested_video_overlay", BOOLEAN, FALSE, true),

    // Haptic Feedback
    DISABLE_HAPTIC_FEEDBACK_CHAPTERS("revanced_disable_haptic_feedback_chapters", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_SCRUBBING("revanced_disable_haptic_feedback_scrubbing", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_SEEK("revanced_disable_haptic_feedback_seek", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_ZOOM("revanced_disable_haptic_feedback_zoom", BOOLEAN, FALSE),


    // Seekbar
    ENABLE_CUSTOM_SEEKBAR_COLOR("revanced_enable_custom_seekbar_color", BOOLEAN, TRUE, true),
    ENABLE_CUSTOM_SEEKBAR_COLOR_VALUE("revanced_custom_seekbar_color_value", STRING, "#ff0000", true,
            parents(ENABLE_CUSTOM_SEEKBAR_COLOR)),
    ENABLE_SEEKBAR_TAPPING("revanced_enable_seekbar_tapping", BOOLEAN, TRUE),
    ENABLE_NEW_THUMBNAIL_PREVIEW("revanced_enable_new_thumbnail_preview", BOOLEAN, FALSE, true),
    ENABLE_TIME_STAMP_SPEED("revanced_enable_time_stamp_speed", BOOLEAN, TRUE),
    HIDE_SEEKBAR("revanced_hide_seekbar", BOOLEAN, FALSE),
    HIDE_SEEKBAR_THUMBNAIL("revanced_hide_seekbar_thumbnail", BOOLEAN, FALSE),
    HIDE_TIME_STAMP("revanced_hide_time_stamp", BOOLEAN, FALSE, true),


    // Shorts
    DISABLE_STARTUP_SHORTS_PLAYER("revanced_disable_startup_shorts_player", BOOLEAN, TRUE),
    ENABLE_NEW_COMMENT_POPUP_PANELS("revanced_enable_new_comment_popup_panels", BOOLEAN, FALSE, true),
    HIDE_SHORTS_PLAYER_COMMENTS_BUTTON("revanced_hide_shorts_player_comments_button", BOOLEAN, FALSE),
    HIDE_SHORTS_PLAYER_DISLIKE_BUTTON("revanced_hide_shorts_player_dislike_button", BOOLEAN, FALSE),
    HIDE_SHORTS_PLAYER_INFO_PANEL("revanced_hide_shorts_player_info_panel", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_JOIN_BUTTON("revanced_hide_shorts_player_join_button", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_LIKE_BUTTON("revanced_hide_shorts_player_like_button", BOOLEAN, FALSE),
    HIDE_SHORTS_PLAYER_PAID_PROMOTION("revanced_hide_shorts_player_paid_promotion_banner", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_REMIX_BUTTON("revanced_hide_shorts_player_remix_button", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_SHARE_BUTTON("revanced_hide_shorts_player_share_button", BOOLEAN, FALSE),
    HIDE_SHORTS_PLAYER_SUBSCRIPTIONS_BUTTON("revanced_hide_shorts_player_subscriptions_button", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_THANKS_BUTTON("revanced_hide_shorts_player_thanks_button", BOOLEAN, TRUE),
    HIDE_SHORTS_PLAYER_PIVOT_BUTTON("revanced_hide_shorts_player_pivot_button", BOOLEAN, TRUE),
    HIDE_SHORTS_SHELF("revanced_hide_shorts_shelf", BOOLEAN, TRUE),

    // Experimental Flags
    HIDE_SHORTS_PLAYER_NAVIGATION_BAR("revanced_hide_shorts_player_navigation_bar", BOOLEAN, FALSE, true),
    HIDE_SHORTS_PLAYER_TOOLBAR("revanced_hide_shorts_player_toolbar", BOOLEAN, FALSE, true),


    // Swipe controls
    ENABLE_SWIPE_BRIGHTNESS("revanced_enable_swipe_brightness", BOOLEAN, TRUE, true),
    ENABLE_SWIPE_VOLUME("revanced_enable_swipe_volume", BOOLEAN, TRUE, true),
    ENABLE_SWIPE_AUTO_BRIGHTNESS("revanced_enable_swipe_auto_brightness", BOOLEAN, FALSE,
            parents(ENABLE_SWIPE_BRIGHTNESS)),
    ENABLE_PRESS_TO_SWIPE("revanced_enable_press_to_swipe", BOOLEAN, FALSE,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    ENABLE_SWIPE_HAPTIC_FEEDBACK("revanced_enable_swipe_haptic_feedback", BOOLEAN, TRUE, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    SWIPE_MAGNITUDE_THRESHOLD("revanced_swipe_magnitude_threshold", INTEGER, 0, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    SWIPE_OVERLAY_BACKGROUND_ALPHA("revanced_swipe_overlay_background_alpha", INTEGER, 127, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    SWIPE_OVERLAY_TEXT_SIZE("revanced_swipe_overlay_text_size", INTEGER, 27, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    SWIPE_OVERLAY_TIMEOUT("revanced_swipe_overlay_timeout", LONG, 500L, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),

    //Experimental Flags
    DISABLE_HDR_AUTO_BRIGHTNESS("revanced_disable_hdr_auto_brightness", BOOLEAN, TRUE, true,
            parents(ENABLE_SWIPE_BRIGHTNESS)),
    ENABLE_SAVE_BRIGHTNESS("revanced_enable_save_brightness", BOOLEAN, TRUE, true,
            parents(ENABLE_SWIPE_BRIGHTNESS)),
    SWIPE_BRIGHTNESS_VALUE("revanced_swipe_brightness_value", FLOAT, 50F),


    // Video
    DEFAULT_PLAYBACK_SPEED("revanced_default_playback_speed", FLOAT, -2.0f),
    DEFAULT_VIDEO_QUALITY_MOBILE("revanced_default_video_quality_mobile", INTEGER, -2),
    DEFAULT_VIDEO_QUALITY_WIFI("revanced_default_video_quality_wifi", INTEGER, -2),
    DISABLE_HDR_VIDEO("revanced_disable_hdr_video", BOOLEAN, FALSE, true),
    DISABLE_DEFAULT_PLAYBACK_SPEED_LIVE("revanced_disable_default_playback_speed_live", BOOLEAN, TRUE),
    ENABLE_CUSTOM_PLAYBACK_SPEED("revanced_enable_custom_playback_speed", BOOLEAN, FALSE, true),
    CUSTOM_PLAYBACK_SPEEDS("revanced_custom_playback_speeds", STRING,
            "0.25\n0.5\n0.75\n1.0\n1.25\n1.5\n1.75\n2.0\n2.25\n2.5", true,
            parents(ENABLE_CUSTOM_PLAYBACK_SPEED)),
    ENABLE_SAVE_PLAYBACK_SPEED("revanced_enable_save_playback_speed", BOOLEAN, TRUE),
    ENABLE_SAVE_VIDEO_QUALITY("revanced_enable_save_video_quality", BOOLEAN, TRUE),


    // Return YouTube Dislike
    RYD_USER_ID("ryd_user_id", STRING, "", RYD),
    RYD_ENABLED("ryd_enabled", BOOLEAN, TRUE, RYD),
    RYD_SHORTS("ryd_shorts", BOOLEAN, TRUE, RYD),
    RYD_DISLIKE_PERCENTAGE("ryd_dislike_percentage", BOOLEAN, FALSE, RYD),
    RYD_COMPACT_LAYOUT("ryd_compact_layout", BOOLEAN, FALSE, RYD),


    // SponsorBlock
    SB_ENABLED("sb-enabled", BOOLEAN, TRUE, SPONSOR_BLOCK),
    SB_VOTING_ENABLED("sb-voting-enabled", BOOLEAN, FALSE, SPONSOR_BLOCK),

    SB_CREATE_NEW_SEGMENT_ENABLED("sb-new-segment-enabled", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_USE_COMPACT_SKIP_BUTTON("sb-use-compact-skip-button", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_AUTO_HIDE_SKIP_BUTTON("sb-auto-hide-skip-segment-button", BOOLEAN, TRUE, SPONSOR_BLOCK),
    SB_SHOW_TOAST_ON_SKIP("show-toast", BOOLEAN, TRUE, SPONSOR_BLOCK),
    SB_TRACK_SKIP_COUNT("count-skips", BOOLEAN, TRUE, SPONSOR_BLOCK),
    SB_UUID("uuid", STRING, "", SPONSOR_BLOCK),
    SB_ADJUST_NEW_SEGMENT_STEP("new-segment-step-accuracy", INTEGER, 150, SPONSOR_BLOCK),
    SB_MIN_DURATION("sb-min-duration", FLOAT, 0F, SPONSOR_BLOCK),
    SB_SEEN_GUIDELINES("sb-seen-gl", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_SKIPPED_SEGMENTS_NUMBER_SKIPPED("sb-skipped-segments", INTEGER, 0, SPONSOR_BLOCK),
    SB_SKIPPED_SEGMENTS_TIME_SAVED("sb-skipped-segments-time", LONG, 0L, SPONSOR_BLOCK),
    SB_SHOW_TIME_WITHOUT_SEGMENTS("sb-length-without-segments", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_IS_VIP("sb-is-vip", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_LAST_VIP_CHECK("sb-last-vip-check", LONG, 0L, SPONSOR_BLOCK),
    SB_API_URL("sb-api-host-url", STRING, "https://sponsor.ajay.app", SPONSOR_BLOCK),
    SB_FIRST_RUN("sb-first-run", BOOLEAN, FALSE, SPONSOR_BLOCK);


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
    public final SharedPrefHelper.SharedPrefNames sharedPref;
    @NonNull
    public final ReturnType returnType;
    /**
     * If the app should be rebooted, if this setting is changed
     */
    public final boolean rebootApp;
    /**
     * Confirmation message to display, if the user tries to change the setting from the default value.
     * Can only be used for {@link ReturnType#BOOLEAN} setting types.
     */
    @Nullable
    public final StringRef userDialogMessage;
    @Nullable
    private final SettingsEnum[] parents;
    // Must be volatile, as some settings are read/write from different threads.
    // Of note, the object value is persistently stored using SharedPreferences (which is thread safe).
    @NonNull
    private volatile Object value;

    SettingsEnum(String path, ReturnType returnType, Object defaultValue) {
        this(path, returnType, defaultValue, REVANCED, false, null, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 boolean rebootApp) {
        this(path, returnType, defaultValue, REVANCED, rebootApp, null, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 SettingsEnum[] parents) {
        this(path, returnType, defaultValue, REVANCED, false, null, parents);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 boolean rebootApp, String userDialogMessage) {
        this(path, returnType, defaultValue, REVANCED, rebootApp, userDialogMessage, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 boolean rebootApp, SettingsEnum[] parents) {
        this(path, returnType, defaultValue, REVANCED, rebootApp, null, parents);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefHelper.SharedPrefNames prefName) {
        this(path, returnType, defaultValue, prefName, false, null, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefHelper.SharedPrefNames prefName,
                 boolean rebootApp, @Nullable String userDialogMessage, @Nullable SettingsEnum[] parents) {
        this.path = Objects.requireNonNull(path);
        this.returnType = Objects.requireNonNull(returnType);
        this.value = this.defaultValue = Objects.requireNonNull(defaultValue);
        this.sharedPref = Objects.requireNonNull(prefName);
        this.rebootApp = rebootApp;

        if (userDialogMessage == null) {
            this.userDialogMessage = null;
        } else {
            if (returnType != ReturnType.BOOLEAN) {
                throw new IllegalArgumentException("must be Boolean type: " + path);
            }
            this.userDialogMessage = new StringRef(userDialogMessage);
        }

        this.parents = parents;

        if (parents != null) {
            for (SettingsEnum parent : parents) {
                if (parent.returnType != ReturnType.BOOLEAN) {
                    throw new IllegalArgumentException("parent must be Boolean type: " + parent);
                }
            }
        }
    }

    private static SettingsEnum[] parents(SettingsEnum... parents) {
        return parents;
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

    public static void setValue(@NonNull SettingsEnum setting, @NonNull String newValue) {
        Objects.requireNonNull(newValue);
        switch (setting.returnType) {
            case BOOLEAN -> setting.value = Boolean.valueOf(newValue);
            case INTEGER -> setting.value = Integer.valueOf(newValue);
            case LONG -> setting.value = Long.valueOf(newValue);
            case FLOAT -> setting.value = Float.valueOf(newValue);
            case STRING -> setting.value = newValue;
            default -> throw new IllegalStateException(setting.name());
        }
    }

    /**
     * This method is only to be used by the Settings preference code.
     */
    public static void setValue(@NonNull SettingsEnum setting, @NonNull Boolean newValue) {
        setting.returnType.validate(newValue);
        setting.value = newValue;
    }

    private void load() {
        switch (returnType) {
            case BOOLEAN ->
                    value = SharedPrefHelper.getBoolean(sharedPref, path, (boolean) defaultValue);
            case INTEGER ->
                    value = SharedPrefHelper.getInt(sharedPref, path, (Integer) defaultValue);
            case LONG -> value = SharedPrefHelper.getLong(sharedPref, path, (Long) defaultValue);
            case FLOAT -> value = SharedPrefHelper.getFloat(sharedPref, path, (Float) defaultValue);
            case STRING ->
                    value = SharedPrefHelper.getString(sharedPref, path, (String) defaultValue);
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
            case BOOLEAN -> SharedPrefHelper.saveBoolean(sharedPref, path, (boolean) newValue);
            case INTEGER, LONG, FLOAT, STRING ->
                    SharedPrefHelper.saveString(sharedPref, path, newValue.toString());
            default -> throw new IllegalStateException(name());
        }
    }

    /**
     * @return if this setting can be configured and used.
     * <p>
     * Not to be confused with {@link #getBoolean()}
     */
    public boolean isAvailable() {
        if (parents == null) {
            return true;
        }
        for (SettingsEnum parent : parents) {
            if (parent.getBoolean()) return true;
        }
        return false;
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
