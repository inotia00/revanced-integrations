package app.revanced.integrations.youtube.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.integrations.youtube.settings.SettingsEnum.ReturnType.BOOLEAN;
import static app.revanced.integrations.youtube.settings.SettingsEnum.ReturnType.FLOAT;
import static app.revanced.integrations.youtube.settings.SettingsEnum.ReturnType.INTEGER;
import static app.revanced.integrations.youtube.settings.SettingsEnum.ReturnType.LONG;
import static app.revanced.integrations.youtube.settings.SettingsEnum.ReturnType.STRING;
import static app.revanced.integrations.youtube.settings.SharedPrefCategory.RETURN_YOUTUBE_DISLIKE;
import static app.revanced.integrations.youtube.settings.SharedPrefCategory.REVANCED;
import static app.revanced.integrations.youtube.settings.SharedPrefCategory.SPONSOR_BLOCK;
import static app.revanced.integrations.youtube.settings.SharedPrefCategory.YOUTUBE;
import static app.revanced.integrations.youtube.utils.StringRef.str;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import app.revanced.integrations.youtube.settingsmenu.ReVancedSettingsFragment;
import app.revanced.integrations.youtube.sponsorblock.SponsorBlockSettings;
import app.revanced.integrations.youtube.utils.LogHelper;
import app.revanced.integrations.youtube.utils.ReVancedUtils;


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
    // Experimental Flags
    CLOSE_INTERSTITIAL_ADS("revanced_close_interstitial_ads", BOOLEAN, FALSE, true),


    // Alternative Thumbnails
    ALT_THUMBNAIL_DEARROW("revanced_alt_thumbnail_dearrow", BOOLEAN, FALSE),
    ALT_THUMBNAIL_DEARROW_API_URL("revanced_alt_thumbnail_dearrow_api_url", STRING,
            "https://dearrow-thumb.ajay.app/api/v1/getThumbnail", true, parents(ALT_THUMBNAIL_DEARROW)),
    ALT_THUMBNAIL_DEARROW_CONNECTION_TOAST("revanced_alt_thumbnail_dearrow_connection_toast", BOOLEAN, FALSE, parents(ALT_THUMBNAIL_DEARROW)),
    ALT_THUMBNAIL_STILLS("revanced_alt_thumbnail_stills", BOOLEAN, FALSE),
    ALT_THUMBNAIL_STILLS_FAST("revanced_alt_thumbnail_stills_fast", BOOLEAN, FALSE, parents(ALT_THUMBNAIL_STILLS)),
    ALT_THUMBNAIL_STILLS_TIME("revanced_alt_thumbnail_stills_time", INTEGER, 2, parents(ALT_THUMBNAIL_STILLS)),


    // Bottom Player
    ENABLE_BOTTOM_PLAYER_GESTURES("revanced_enable_bottom_player_gestures", BOOLEAN, TRUE, true),

    // Channel Bar
    HIDE_JOIN_BUTTON("revanced_hide_join_button", BOOLEAN, TRUE),
    HIDE_START_TRIAL_BUTTON("revanced_hide_start_trial_button", BOOLEAN, TRUE),

    // Button Container
    HIDE_CREATE_CLIP_BUTTON("revanced_hide_button_create_clip", BOOLEAN, FALSE),
    HIDE_DOWNLOAD_BUTTON("revanced_hide_button_download", BOOLEAN, FALSE),
    HIDE_LIKE_DISLIKE_BUTTON("revanced_hide_button_like_dislike", BOOLEAN, FALSE),
    HIDE_REWARDS_BUTTON("revanced_hide_button_rewards", BOOLEAN, FALSE),
    HIDE_REMIX_BUTTON("revanced_hide_button_remix", BOOLEAN, FALSE),
    HIDE_REPORT_BUTTON("revanced_hide_button_report", BOOLEAN, FALSE),
    HIDE_SAVE_TO_PLAYLIST_BUTTON("revanced_hide_button_save_to_playlist", BOOLEAN, FALSE),
    HIDE_SHARE_BUTTON("revanced_hide_button_share", BOOLEAN, FALSE),
    HIDE_SHOP_BUTTON("revanced_hide_button_shop", BOOLEAN, FALSE),
    HIDE_THANKS_BUTTON("revanced_hide_button_thanks", BOOLEAN, FALSE),
    HIDE_TRANSCRIPT_BUTTON("revanced_hide_button_transcript", BOOLEAN, FALSE),

    // Comments
    HIDE_CHANNEL_GUIDELINES("revanced_hide_channel_guidelines", BOOLEAN, TRUE),
    HIDE_COMMENTS_BY_MEMBERS("revanced_hide_comments_by_members", BOOLEAN, FALSE),
    HIDE_COMMENTS_SECTION("revanced_hide_comments_section", BOOLEAN, FALSE),
    HIDE_COMMENTS_THANKS_BUTTON("revanced_hide_comments_thanks_button", BOOLEAN, FALSE),
    HIDE_CREATE_SHORTS_BUTTON("revanced_hide_create_shorts_button", BOOLEAN, FALSE),
    HIDE_EMOJI_PICKER("revanced_hide_emoji_picker", BOOLEAN, FALSE),
    HIDE_PREVIEW_COMMENT("revanced_hide_preview_comment", BOOLEAN, FALSE),
    HIDE_PREVIEW_COMMENT_TYPE("revanced_hide_preview_comment_type", BOOLEAN, FALSE),
    HIDE_PREVIEW_COMMENT_OLD_METHOD("revanced_hide_preview_comment_old_method", BOOLEAN, FALSE),
    HIDE_PREVIEW_COMMENT_NEW_METHOD("revanced_hide_preview_comment_new_method", BOOLEAN, FALSE),


    // Flyout Panel

    // Feed Flyout Panel
    HIDE_FEED_FLYOUT_PANEL("revanced_hide_feed_flyout_panel", BOOLEAN, FALSE),
    HIDE_FEED_FLYOUT_PANEL_FILTER_STRINGS("revanced_hide_feed_flyout_panel_filter_strings", STRING, "", true,
            parents(HIDE_FEED_FLYOUT_PANEL)),

    // Player Flyout Panel
    CHANGE_PLAYER_FLYOUT_PANEL_TOGGLE("revanced_change_player_flyout_panel_toggle", BOOLEAN, TRUE, true),
    ENABLE_OLD_QUALITY_LAYOUT("revanced_enable_old_quality_layout", BOOLEAN, TRUE, true),
    HIDE_PLAYER_FLYOUT_PANEL_AUDIO_TRACK("revanced_hide_player_flyout_panel_audio_track", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_CAPTIONS("revanced_hide_player_flyout_panel_captions", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_CAPTIONS_FOOTER("revanced_hide_player_flyout_panel_captions_footer", BOOLEAN, FALSE, true),
    HIDE_PLAYER_FLYOUT_PANEL_LOCK_SCREEN("revanced_hide_player_flyout_panel_lock_screen", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_MORE("revanced_hide_player_flyout_panel_more_info", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_PLAYBACK_SPEED("revanced_hide_player_flyout_panel_playback_speed", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_QUALITY_FOOTER("revanced_hide_player_flyout_panel_quality_footer", BOOLEAN, FALSE, true),
    HIDE_PLAYER_FLYOUT_PANEL_REPORT("revanced_hide_player_flyout_panel_report", BOOLEAN, TRUE),

    // Player Flyout Panel (Additional settings)
    HIDE_PLAYER_FLYOUT_PANEL_ADDITIONAL_SETTINGS("revanced_hide_player_flyout_panel_additional_settings", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_AMBIENT("revanced_hide_player_flyout_panel_ambient_mode", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_HELP("revanced_hide_player_flyout_panel_help", BOOLEAN, TRUE),
    HIDE_PLAYER_FLYOUT_PANEL_LOOP("revanced_hide_player_flyout_panel_loop_video", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_PREMIUM_CONTROLS("revanced_hide_player_flyout_panel_premium_controls", BOOLEAN, TRUE),
    HIDE_PLAYER_FLYOUT_PANEL_STABLE_VOLUME("revanced_hide_player_flyout_panel_stable_volume", BOOLEAN, FALSE),
    HIDE_PLAYER_FLYOUT_PANEL_STATS_FOR_NERDS("revanced_hide_player_flyout_panel_stats_for_nerds", BOOLEAN, TRUE),
    HIDE_PLAYER_FLYOUT_PANEL_WATCH_IN_VR("revanced_hide_player_flyout_panel_watch_in_vr", BOOLEAN, TRUE),
    HIDE_PLAYER_FLYOUT_PANEL_YT_MUSIC("revanced_hide_player_flyout_panel_listen_with_youtube_music", BOOLEAN, TRUE),


    // Fullscreen
    DISABLE_AMBIENT_MODE_IN_FULLSCREEN("revanced_disable_ambient_mode_in_fullscreen", BOOLEAN, FALSE, true),
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
    HIDE_QUICK_ACTIONS_OPEN_MIX_PLAYLIST_BUTTON("revanced_hide_quick_actions_open_mix_playlist", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_OPEN_PLAYLIST_BUTTON("revanced_hide_quick_actions_open_playlist", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_RELATED_VIDEO("revanced_hide_quick_actions_related_videos", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_SAVE_TO_PLAYLIST_BUTTON("revanced_hide_quick_actions_save_to_playlist", BOOLEAN, FALSE),
    HIDE_QUICK_ACTIONS_SHARE_BUTTON("revanced_hide_quick_actions_share", BOOLEAN, FALSE),
    QUICK_ACTIONS_MARGIN_TOP("revanced_quick_actions_margin_top", INTEGER, 12, true),

    // Experimental Flags
    DISABLE_LANDSCAPE_MODE("revanced_disable_landscape_mode", BOOLEAN, FALSE, true),
    ENABLE_COMPACT_CONTROLS_OVERLAY("revanced_enable_compact_controls_overlay", BOOLEAN, FALSE, true),
    FORCE_FULLSCREEN("revanced_force_fullscreen", BOOLEAN, FALSE, true),
    KEEP_LANDSCAPE_MODE("revanced_keep_landscape_mode", BOOLEAN, FALSE, true),
    KEEP_LANDSCAPE_MODE_TIMEOUT("revanced_keep_landscape_mode_timeout", LONG, 3000L, true),


    // General
    CHANGE_START_PAGE("revanced_change_start_page", STRING, "", true),
    DISABLE_AUTO_CAPTIONS("revanced_disable_auto_captions", BOOLEAN, FALSE, true),
    ENABLE_GRADIENT_LOADING_SCREEN("revanced_enable_gradient_loading_screen", BOOLEAN, FALSE, true),
    ENABLE_SONG_SEARCH("revanced_enable_song_search", BOOLEAN, TRUE, true),
    ENABLE_TABLET_MINI_PLAYER("revanced_enable_tablet_mini_player", BOOLEAN, FALSE, true),
    ENABLE_WIDE_SEARCH_BAR("revanced_enable_wide_search_bar", BOOLEAN, FALSE, true),
    ENABLE_WIDE_SEARCH_BAR_IN_YOU_TAB("revanced_enable_wide_search_bar_in_you_tab", BOOLEAN, FALSE, true),
    HIDE_ACCOUNT_MENU("revanced_hide_account_menu", BOOLEAN, FALSE),
    HIDE_ACCOUNT_MENU_FILTER_STRINGS("revanced_hide_account_menu_filter_strings", STRING, "", true,
            parents(HIDE_ACCOUNT_MENU)),
    HIDE_AUTO_PLAYER_POPUP_PANELS("revanced_hide_auto_player_popup_panels", BOOLEAN, TRUE, true),
    HIDE_CATEGORY_BAR_IN_FEED("revanced_hide_category_bar_in_feed", BOOLEAN, FALSE, true),
    HIDE_CATEGORY_BAR_IN_RELATED_VIDEO("revanced_hide_category_bar_in_related_video", BOOLEAN, FALSE, true),
    HIDE_CATEGORY_BAR_IN_SEARCH_RESULTS("revanced_hide_category_bar_in_search_results", BOOLEAN, FALSE, true),
    HIDE_CHANNEL_LIST_SUBMENU("revanced_hide_channel_list_submenu", BOOLEAN, FALSE, true),
    HIDE_CROWDFUNDING_BOX("revanced_hide_crowdfunding_box", BOOLEAN, TRUE, true),
    HIDE_FLOATING_MICROPHONE("revanced_hide_floating_microphone", BOOLEAN, TRUE, true),
    HIDE_HANDLE("revanced_hide_handle", BOOLEAN, TRUE, true),
    HIDE_LATEST_VIDEOS_BUTTON("revanced_hide_latest_videos_button", BOOLEAN, TRUE),
    HIDE_LOAD_MORE_BUTTON("revanced_hide_load_more_button", BOOLEAN, TRUE, true),
    HIDE_MIX_PLAYLISTS("revanced_hide_mix_playlists", BOOLEAN, FALSE),
    HIDE_SEARCH_TERM_THUMBNAIL("revanced_hide_search_term_thumbnail", BOOLEAN, FALSE),
    HIDE_SNACK_BAR("revanced_hide_snack_bar", BOOLEAN, FALSE),
    HIDE_TOOLBAR_CREATE_NOTIFICATION_BUTTON("revanced_hide_toolbar_create_notification_button", BOOLEAN, FALSE, true),
    HIDE_TRENDING_SEARCHES("revanced_hide_trending_searches", BOOLEAN, TRUE),
    REMOVE_VIEWER_DISCRETION_DIALOG("revanced_remove_viewer_discretion_dialog", BOOLEAN, FALSE),

    // Layout
    CUSTOM_FILTER("revanced_custom_filter", BOOLEAN, FALSE),
    CUSTOM_FILTER_STRINGS("revanced_custom_filter_strings", STRING, "", true,
            parents(CUSTOM_FILTER)),
    HIDE_ALBUM_CARDS("revanced_hide_album_card", BOOLEAN, TRUE),
    HIDE_CHIPS_SHELF("revanced_hide_chips_shelf", BOOLEAN, TRUE),
    HIDE_COMMUNITY_POSTS_HOME("revanced_hide_community_posts_home", BOOLEAN, TRUE),
    HIDE_COMMUNITY_POSTS_SUBSCRIPTIONS("revanced_hide_community_posts_subscriptions", BOOLEAN, FALSE),
    HIDE_EXPANDABLE_CHIP("revanced_hide_expandable_chip", BOOLEAN, TRUE),
    HIDE_FEED_SURVEY("revanced_hide_feed_survey", BOOLEAN, TRUE),
    HIDE_GRAY_DESCRIPTION("revanced_hide_gray_description", BOOLEAN, TRUE),
    HIDE_GRAY_SEPARATOR("revanced_hide_gray_separator", BOOLEAN, TRUE),
    HIDE_INFO_PANEL("revanced_hide_info_panel", BOOLEAN, TRUE),
    HIDE_NOTIFY_ME_BUTTON("revanced_hide_notify_me_button", BOOLEAN, FALSE),
    HIDE_LATEST_POSTS("revanced_hide_latest_posts", BOOLEAN, TRUE),
    HIDE_MEDICAL_PANEL("revanced_hide_medical_panel", BOOLEAN, TRUE),
    HIDE_MOVIE_SHELF("revanced_hide_movie_shelf", BOOLEAN, FALSE),
    HIDE_SEARCH_BAR("revanced_hide_search_bar", BOOLEAN, FALSE),
    HIDE_TICKET_SHELF("revanced_hide_ticket_shelf", BOOLEAN, TRUE),
    HIDE_TIMED_REACTIONS("revanced_hide_timed_reactions", BOOLEAN, TRUE),
    // Experimental Flags
    HIDE_SUGGESTIONS_SHELF("revanced_hide_suggestions_shelf", BOOLEAN, FALSE, true),
    HIDE_SUGGESTIONS_SHELF_METHOD("revanced_hide_suggestions_shelf_method", BOOLEAN, FALSE, true),
    HIDE_VIDEO_WITH_GRAY_DESCRIPTION("revanced_hide_video_with_gray_description", BOOLEAN, FALSE, true),
    HIDE_VIDEO_WITH_LOW_VIEW("revanced_hide_video_with_low_view", BOOLEAN, FALSE, true),

    // Channel Profile
    HIDE_BROWSE_STORE_BUTTON("revanced_hide_browse_store_button", BOOLEAN, TRUE),
    HIDE_CHANNEL_MEMBER_SHELF("revanced_hide_channel_member_shelf", BOOLEAN, TRUE),
    HIDE_CHANNEL_PROFILE_LINKS("revanced_hide_channel_profile_links", BOOLEAN, TRUE),
    HIDE_FOR_YOU_SHELF("revanced_hide_for_you_shelf", BOOLEAN, TRUE),
    HIDE_STORE_TAB("revanced_hide_store_tab", BOOLEAN, TRUE),

    // Description
    HIDE_CHAPTERS("revanced_hide_chapters", BOOLEAN, FALSE),
    HIDE_INFO_CARDS_SECTION("revanced_hide_info_cards_section", BOOLEAN, FALSE),
    HIDE_GAME_SECTION("revanced_hide_game_section", BOOLEAN, FALSE),
    HIDE_MUSIC_SECTION("revanced_hide_music_section", BOOLEAN, FALSE),
    HIDE_PLACE_SECTION("revanced_hide_place_section", BOOLEAN, FALSE),
    HIDE_PODCAST_SECTION("revanced_hide_podcast_section", BOOLEAN, FALSE),
    HIDE_SHOPPING_LINKS("revanced_hide_shopping_links", BOOLEAN, TRUE),
    HIDE_TRANSCIPT_SECTION("revanced_hide_transcript_section", BOOLEAN, FALSE),


    // Misc
    BYPASS_AMBIENT_MODE_RESTRICTIONS("revanced_bypass_ambient_mode_restrictions", BOOLEAN, FALSE),
    DISABLE_AMBIENT_MODE("revanced_disable_ambient_mode", BOOLEAN, FALSE, true),
    DISABLE_UPDATE_SCREEN("revanced_disable_update_screen", BOOLEAN, TRUE, true),
    ENABLE_DEBUG_LOGGING("revanced_enable_debug_logging", BOOLEAN, FALSE),
    ENABLE_DEBUG_BUFFER_LOGGING("revanced_enable_debug_buffer_logging", BOOLEAN, FALSE),
    ENABLE_EXTERNAL_BROWSER("revanced_enable_external_browser", BOOLEAN, TRUE, true),
    ENABLE_LANGUAGE_SWITCH("revanced_enable_language_switch", BOOLEAN, TRUE, true),
    ENABLE_NEW_SPLASH_ANIMATION("revanced_enable_new_splash_animation", BOOLEAN, TRUE, true),
    ENABLE_OPEN_LINKS_DIRECTLY("revanced_enable_open_links_directly", BOOLEAN, TRUE),
    DOUBLE_BACK_TIMEOUT("revanced_double_back_timeout", INTEGER, 2, true),
    SANITIZE_SHARING_LINKS("revanced_sanitize_sharing_links", BOOLEAN, true, true),

    // Experimental Flags
    DISABLE_QUIC_PROTOCOL("revanced_disable_quic_protocol", BOOLEAN, FALSE, true),
    ENABLE_OPUS_CODEC("revanced_enable_opus_codec", BOOLEAN, FALSE, true),
    ENABLE_PHONE_LAYOUT("revanced_enable_phone_layout", BOOLEAN, FALSE, true),
    ENABLE_TABLET_LAYOUT("revanced_enable_tablet_layout", BOOLEAN, FALSE, true),
    ENABLE_VIDEO_CODEC("revanced_enable_video_codec", BOOLEAN, FALSE, true),
    ENABLE_VIDEO_CODEC_TYPE("revanced_enable_video_codec_type", BOOLEAN, FALSE, true),
    SPOOF_APP_VERSION("revanced_spoof_app_version", BOOLEAN, FALSE, true),
    SPOOF_APP_VERSION_TARGET("revanced_spoof_app_version_target", STRING, "18.17.43", true,
            parents(SPOOF_APP_VERSION)),
    SPOOF_DEVICE_DIMENSIONS("revanced_spoof_device_dimensions", BOOLEAN, FALSE, true),
    SPOOF_PLAYER_PARAMETER("revanced_spoof_player_parameter", BOOLEAN, TRUE, true),
    SPOOF_PLAYER_PARAMETER_IN_FEED("revanced_spoof_player_parameter_in_feed", BOOLEAN, FALSE, true),


    // Navigation
    ENABLE_TABLET_NAVIGATION_BAR("revanced_enable_tablet_navigation_bar", BOOLEAN, FALSE, true),
    HIDE_CREATE_BUTTON("revanced_hide_create_button", BOOLEAN, TRUE, true),
    HIDE_HOME_BUTTON("revanced_hide_home_button", BOOLEAN, FALSE, true),
    HIDE_LIBRARY_BUTTON("revanced_hide_library_button", BOOLEAN, FALSE, true),
    HIDE_NAVIGATION_LABEL("revanced_hide_navigation_label", BOOLEAN, FALSE, true),
    HIDE_NOTIFICATIONS_BUTTON("revanced_hide_notifications_button", BOOLEAN, FALSE, true),
    HIDE_SHORTS_BUTTON("revanced_hide_shorts_button", BOOLEAN, FALSE, true),
    HIDE_SUBSCRIPTIONS_BUTTON("revanced_hide_subscriptions_button", BOOLEAN, FALSE, true),
    SWITCH_CREATE_NOTIFICATION("revanced_switching_create_notification", BOOLEAN, TRUE, true),

    // Settings not exported
    INITIALIZED("revanced_initialized", BOOLEAN, FALSE, YOUTUBE),


    // Overlay Button
    ALWAYS_REPEAT("revanced_always_repeat", BOOLEAN, FALSE),
    OVERLAY_BUTTON_ALWAYS_REPEAT("revanced_overlay_button_always_repeat", BOOLEAN, FALSE),
    OVERLAY_BUTTON_COPY_VIDEO_URL("revanced_overlay_button_copy_video_url", BOOLEAN, FALSE),
    OVERLAY_BUTTON_COPY_VIDEO_URL_TIMESTAMP("revanced_overlay_button_copy_video_url_timestamp", BOOLEAN, FALSE),
    OVERLAY_BUTTON_EXTERNAL_DOWNLOADER("revanced_overlay_button_external_downloader", BOOLEAN, FALSE),
    OVERLAY_BUTTON_SPEED_DIALOG("revanced_overlay_button_speed_dialog", BOOLEAN, FALSE),
    EXTERNAL_DOWNLOADER_PACKAGE_NAME("revanced_external_downloader_package_name", STRING, "com.deniscerri.ytdl", true),

    // Experimental Flags
    HOOK_DOWNLOAD_BUTTON("revanced_hook_download_button", BOOLEAN, FALSE),


    // Player
    CUSTOM_PLAYER_OVERLAY_OPACITY("revanced_custom_player_overlay_opacity", INTEGER, 100, true),
    DISABLE_SPEED_OVERLAY("revanced_disable_speed_overlay", BOOLEAN, FALSE, true),
    HIDE_AUDIO_TRACK_BUTTON("revanced_hide_audio_track_button", BOOLEAN, TRUE),
    HIDE_AUTOPLAY_BUTTON("revanced_hide_autoplay_button", BOOLEAN, TRUE, true),
    HIDE_CAPTIONS_BUTTON("revanced_hide_captions_button", BOOLEAN, FALSE, true),
    HIDE_CAST_BUTTON("revanced_hide_cast_button", BOOLEAN, TRUE, true),
    HIDE_CHANNEL_WATERMARK("revanced_hide_channel_watermark", BOOLEAN, TRUE),
    HIDE_COLLAPSE_BUTTON("revanced_hide_collapse_button", BOOLEAN, FALSE),
    HIDE_END_SCREEN_CARDS("revanced_hide_end_screen_cards", BOOLEAN, FALSE, true),
    HIDE_INFO_CARDS("revanced_hide_info_cards", BOOLEAN, FALSE, true),
    HIDE_PREVIOUS_NEXT_BUTTON("revanced_hide_previous_next_button", BOOLEAN, FALSE),
    HIDE_SEEK_MESSAGE("revanced_hide_seek_message", BOOLEAN, FALSE, true),
    HIDE_SEEK_UNDO_MESSAGE("revanced_hide_seek_undo_message", BOOLEAN, FALSE, true),
    HIDE_SUGGESTED_ACTION("revanced_hide_suggested_actions", BOOLEAN, TRUE, true),
    HIDE_SUGGESTED_VIDEO_OVERLAY("revanced_hide_suggested_video_overlay", BOOLEAN, FALSE, true),
    HIDE_SUGGESTED_VIDEO_OVERLAY_AUTO_PLAY("revanced_hide_suggested_video_overlay_auto_play", BOOLEAN, TRUE, true,
            parents(HIDE_SUGGESTED_VIDEO_OVERLAY)),

    HIDE_YOUTUBE_MUSIC_BUTTON("revanced_hide_youtube_music_button", BOOLEAN, FALSE),

    // Experimental Flags
    HIDE_FILMSTRIP_OVERLAY("revanced_hide_filmstrip_overlay", BOOLEAN, FALSE, true),

    // Haptic Feedback
    DISABLE_HAPTIC_FEEDBACK_CHAPTERS("revanced_disable_haptic_feedback_chapters", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_SCRUBBING("revanced_disable_haptic_feedback_scrubbing", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_SEEK("revanced_disable_haptic_feedback_seek", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_SEEK_UNDO("revanced_disable_haptic_feedback_seek_undo", BOOLEAN, FALSE),
    DISABLE_HAPTIC_FEEDBACK_ZOOM("revanced_disable_haptic_feedback_zoom", BOOLEAN, FALSE),


    // Seekbar
    APPEND_TIME_STAMP_INFORMATION("revanced_append_time_stamp_information", BOOLEAN, TRUE),
    APPEND_TIME_STAMP_INFORMATION_TYPE("revanced_append_time_stamp_information_type", BOOLEAN, TRUE),
    ENABLE_CUSTOM_SEEKBAR_COLOR("revanced_enable_custom_seekbar_color", BOOLEAN, TRUE, true),
    ENABLE_CUSTOM_SEEKBAR_COLOR_VALUE("revanced_custom_seekbar_color_value", STRING, "#ff0000", true,
            parents(ENABLE_CUSTOM_SEEKBAR_COLOR)),
    ENABLE_SEEKBAR_TAPPING("revanced_enable_seekbar_tapping", BOOLEAN, TRUE),
    ENABLE_NEW_THUMBNAIL_PREVIEW("revanced_enable_new_thumbnail_preview", BOOLEAN, FALSE, true),
    HIDE_SEEKBAR("revanced_hide_seekbar", BOOLEAN, FALSE),
    HIDE_SEEKBAR_THUMBNAIL("revanced_hide_seekbar_thumbnail", BOOLEAN, FALSE),
    HIDE_TIME_STAMP("revanced_hide_time_stamp", BOOLEAN, FALSE, true),


    // Shorts
    DISABLE_STARTUP_SHORTS_PLAYER("revanced_disable_startup_shorts_player", BOOLEAN, TRUE),
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
    HIDE_SHORTS_TOOLBAR_BANNER("revanced_hide_shorts_toolbar_banner", BOOLEAN, FALSE, true),
    HIDE_SHORTS_TOOLBAR_CAMERA_BUTTON("revanced_hide_shorts_toolbar_camera_button", BOOLEAN, FALSE, true),
    HIDE_SHORTS_TOOLBAR_MENU_BUTTON("revanced_hide_shorts_toolbar_menu_button", BOOLEAN, FALSE, true),
    HIDE_SHORTS_TOOLBAR_SEARCH_BUTTON("revanced_hide_shorts_toolbar_search_button", BOOLEAN, FALSE, true),
    HIDE_SHORTS_SHELF("revanced_hide_shorts_shelf", BOOLEAN, TRUE),

    // Experimental Flags
    HIDE_SHORTS_PLAYER_NAVIGATION_BAR("revanced_hide_shorts_player_navigation_bar", BOOLEAN, FALSE, true),


    // Swipe controls
    ENABLE_SWIPE_BRIGHTNESS("revanced_enable_swipe_brightness", BOOLEAN, TRUE, true),
    ENABLE_SWIPE_VOLUME("revanced_enable_swipe_volume", BOOLEAN, TRUE, true),
    ENABLE_SWIPE_AUTO_BRIGHTNESS("revanced_enable_swipe_auto_brightness", BOOLEAN, FALSE,
            parents(ENABLE_SWIPE_BRIGHTNESS)),
    ENABLE_SWIPE_PRESS_TO_ENGAGE("revanced_enable_swipe_press_to_engage", BOOLEAN, FALSE, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    ENABLE_SWIPE_HAPTIC_FEEDBACK("revanced_enable_swipe_haptic_feedback", BOOLEAN, TRUE, true,
            parents(ENABLE_SWIPE_BRIGHTNESS, ENABLE_SWIPE_VOLUME)),
    SWIPE_LOCK_MODE("revanced_swipe_gestures_lock_mode", BOOLEAN, FALSE, true,
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
    SWIPE_BRIGHTNESS_AUTO("revanced_swipe_brightness_auto", BOOLEAN, TRUE),
    SWIPE_BRIGHTNESS_VALUE("revanced_swipe_brightness_value", FLOAT, 0.5f),


    // Video
    DEFAULT_PLAYBACK_SPEED("revanced_default_playback_speed", FLOAT, -2.0f),
    DEFAULT_VIDEO_QUALITY_MOBILE("revanced_default_video_quality_mobile", INTEGER, -2),
    DEFAULT_VIDEO_QUALITY_WIFI("revanced_default_video_quality_wifi", INTEGER, -2),
    DISABLE_HDR_VIDEO("revanced_disable_hdr_video", BOOLEAN, FALSE, true),
    DISABLE_DEFAULT_PLAYBACK_SPEED_LIVE("revanced_disable_default_playback_speed_live", BOOLEAN, TRUE),
    ENABLE_CUSTOM_PLAYBACK_SPEED("revanced_enable_custom_playback_speed", BOOLEAN, FALSE, true),
    CUSTOM_PLAYBACK_SPEED_PANEL_TYPE("revanced_custom_playback_speed_panel_type", BOOLEAN, FALSE,
            parents(ENABLE_CUSTOM_PLAYBACK_SPEED)),
    CUSTOM_PLAYBACK_SPEEDS("revanced_custom_playback_speeds", STRING,
            "0.25\n0.5\n0.75\n1.0\n1.25\n1.5\n1.75\n2.0\n2.25\n2.5", true,
            parents(ENABLE_CUSTOM_PLAYBACK_SPEED)),
    ENABLE_SAVE_PLAYBACK_SPEED("revanced_enable_save_playback_speed", BOOLEAN, FALSE),
    ENABLE_SAVE_VIDEO_QUALITY("revanced_enable_save_video_quality", BOOLEAN, TRUE),
    // Experimental Flags
    ENABLE_DEFAULT_PLAYBACK_SPEED_SHORTS("revanced_enable_default_playback_speed_shorts", BOOLEAN, FALSE),
    SKIP_PRELOADED_BUFFER("revanced_skip_preloaded_buffer", BOOLEAN, FALSE),
    SKIP_PRELOADED_BUFFER_TOAST("revanced_skip_preloaded_buffer_toast", BOOLEAN, TRUE),


    // Return YouTube Dislike
    RYD_ENABLED("ryd_enabled", BOOLEAN, TRUE, RETURN_YOUTUBE_DISLIKE),
    RYD_USER_ID("ryd_user_id", STRING, "", RETURN_YOUTUBE_DISLIKE),
    RYD_SHORTS("ryd_shorts", BOOLEAN, TRUE, RETURN_YOUTUBE_DISLIKE, parents(RYD_ENABLED)),
    RYD_DISLIKE_PERCENTAGE("ryd_dislike_percentage", BOOLEAN, FALSE, RETURN_YOUTUBE_DISLIKE, parents(RYD_ENABLED)),
    RYD_COMPACT_LAYOUT("ryd_compact_layout", BOOLEAN, FALSE, RETURN_YOUTUBE_DISLIKE, parents(RYD_ENABLED)),
    RYD_TOAST_ON_CONNECTION_ERROR("ryd_toast_on_connection_error", BOOLEAN, FALSE, RETURN_YOUTUBE_DISLIKE, parents(RYD_ENABLED)),


    // SponsorBlock
    SB_ENABLED("sb_enabled", BOOLEAN, TRUE, SPONSOR_BLOCK),
    SB_PRIVATE_USER_ID("sb_private_user_id_Do_Not_Share", STRING, "", SPONSOR_BLOCK),
    /**
     * Do not use directly, instead use {@link SponsorBlockSettings}
     */
    SB_CREATE_NEW_SEGMENT_STEP("sb_create_new_segment_step", INTEGER, 150, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_VOTING_BUTTON("sb_voting_button", BOOLEAN, FALSE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_CREATE_NEW_SEGMENT("sb_create_new_segment", BOOLEAN, FALSE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_COMPACT_SKIP_BUTTON("sb_compact_skip_button", BOOLEAN, FALSE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_AUTO_HIDE_SKIP_BUTTON("sb_auto_hide_skip_button", BOOLEAN, TRUE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_TOAST_ON_SKIP("sb_toast_on_skip", BOOLEAN, TRUE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_TOAST_ON_CONNECTION_ERROR("sb_toast_on_connection_error", BOOLEAN, FALSE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_TRACK_SKIP_COUNT("sb_track_skip_count", BOOLEAN, TRUE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_SEGMENT_MIN_DURATION("sb_min_segment_duration", FLOAT, 0F, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_VIDEO_LENGTH_WITHOUT_SEGMENTS("sb_video_length_without_segments", BOOLEAN, FALSE, SPONSOR_BLOCK, parents(SB_ENABLED)),
    SB_API_URL("sb_api_url", STRING, "https://sponsor.ajay.app", SPONSOR_BLOCK),
    SB_USER_IS_VIP("sb_user_is_vip", BOOLEAN, FALSE, SPONSOR_BLOCK),
    // SB settings not exported
    SB_LAST_VIP_CHECK("sb_last_vip_check", LONG, 0L, SPONSOR_BLOCK),
    SB_HIDE_EXPORT_WARNING("sb_hide_export_warning", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_SEEN_GUIDELINES("sb_seen_guidelines", BOOLEAN, FALSE, SPONSOR_BLOCK),
    SB_LOCAL_TIME_SAVED_NUMBER_SEGMENTS("sb_local_time_saved_number_segments", INTEGER, 0, SPONSOR_BLOCK),
    SB_LOCAL_TIME_SAVED_MILLISECONDS("sb_local_time_saved_milliseconds", LONG, 0L, SPONSOR_BLOCK);

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

    @Nullable
    private final SettingsEnum[] parents;
    // Must be volatile, as some settings are read/write from different threads.
    // Of note, the object value is persistently stored using SharedPreferences (which is thread safe).
    @NonNull
    private volatile Object value;

    SettingsEnum(String path, ReturnType returnType, Object defaultValue) {
        this(path, returnType, defaultValue, REVANCED, false, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 boolean rebootApp) {
        this(path, returnType, defaultValue, REVANCED, rebootApp, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 SettingsEnum[] parents) {
        this(path, returnType, defaultValue, REVANCED, false, parents);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue,
                 boolean rebootApp, SettingsEnum[] parents) {
        this(path, returnType, defaultValue, REVANCED, rebootApp, parents);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefCategory prefName) {
        this(path, returnType, defaultValue, prefName, false, null);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefCategory prefName,
                 SettingsEnum[] parents) {
        this(path, returnType, defaultValue, prefName, false, parents);
    }

    SettingsEnum(String path, ReturnType returnType, Object defaultValue, SharedPrefCategory prefName,
                 boolean rebootApp, @Nullable SettingsEnum[] parents) {
        this.path = Objects.requireNonNull(path);
        this.returnType = Objects.requireNonNull(returnType);
        this.value = this.defaultValue = Objects.requireNonNull(defaultValue);
        this.sharedPref = Objects.requireNonNull(prefName);
        this.rebootApp = rebootApp;
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
            case BOOLEAN:
                value = sharedPref.getBoolean(path, (boolean) defaultValue);
                break;
            case INTEGER:
                value = sharedPref.getIntegerString(path, (Integer) defaultValue);
                break;
            case LONG:
                value = sharedPref.getLongString(path, (Long) defaultValue);
                break;
            case FLOAT:
                value = sharedPref.getFloatString(path, (Float) defaultValue);
                break;
            case STRING:
                value = sharedPref.getString(path, (String) defaultValue);
                break;
            default:
                throw new IllegalStateException(name());
        }
    }

    /**
     * Sets the value, and persistently saves it.
     */
    public void saveValue(@NonNull Object newValue) {
        returnType.validate(newValue);
        value = newValue; // Must set before saving to preferences (otherwise importing fails to update UI correctly).
        switch (returnType) {
            case BOOLEAN:
                sharedPref.saveBoolean(path, (boolean) newValue);
                break;
            case INTEGER:
                sharedPref.saveIntegerString(path, (Integer) newValue);
                break;
            case LONG:
                sharedPref.saveLongString(path, (Long) newValue);
                break;
            case FLOAT:
                sharedPref.saveFloatString(path, (Float) newValue);
                break;
            case STRING:
                sharedPref.saveString(path, (String) newValue);
                break;
            default:
                throw new IllegalStateException(name());
        }
    }

    /**
     * Identical to calling {@link #saveValue(Object)} using {@link #defaultValue}.
     */
    public void resetToDefault() {
        saveValue(defaultValue);
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

    /**
     * @return if the currently set value is different from {@link #defaultValue}
     */
    public boolean isNotSetToDefault() {
        return !value.equals(defaultValue);
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

    /**
     * This could be yet another field,
     * for now use a simple switch statement since this method is not used outside this class.
     */
    private boolean includeWithImportExport() {
        return switch (this) { // Not useful to export, no reason to include it.
            // Not useful to export, no reason to include it.
            case RYD_USER_ID,
                    INITIALIZED,
                    SB_HIDE_EXPORT_WARNING,
                    SB_LAST_VIP_CHECK,
                    SB_SEEN_GUIDELINES -> false;
            default -> true;
        };
    }

    // Begin import / export

    /**
     * If a setting path has this prefix, then remove it before importing/exporting.
     */
    private static final String OPTIONAL_REVANCED_SETTINGS_PREFIX = "revanced_";

    /**
     * The path, minus any 'revanced' prefix to keep json concise.
     */
    private String getImportExportKey() {
        if (path.startsWith(OPTIONAL_REVANCED_SETTINGS_PREFIX)) {
            return path.substring(OPTIONAL_REVANCED_SETTINGS_PREFIX.length());
        }
        return path;
    }

    private static SettingsEnum[] valuesSortedForExport() {
        SettingsEnum[] sorted = values();
        Arrays.sort(sorted, (SettingsEnum o1, SettingsEnum o2) -> {
            // Organize SponsorBlock settings last.
            final boolean o1IsSb = o1.sharedPref == SPONSOR_BLOCK;
            final boolean o2IsSb = o2.sharedPref == SPONSOR_BLOCK;
            if (o1IsSb != o2IsSb) {
                return o1IsSb ? 1 : -1;
            }
            return o1.path.compareTo(o2.path);
        });
        return sorted;
    }

    @NonNull
    public static String exportJSON(@Nullable Context alertDialogContext) {
        try {
            JSONObject json = new JSONObject();
            for (SettingsEnum setting : valuesSortedForExport()) {
                String importExportKey = setting.getImportExportKey();
                if (json.has(importExportKey)) {
                    throw new IllegalArgumentException("duplicate key found: " + importExportKey);
                }
                final boolean exportDefaultValues = false; // Enable to see what all settings looks like in the UI.
                if (setting.includeWithImportExport() && (setting.isNotSetToDefault() | exportDefaultValues)) {
                    json.put(importExportKey, setting.getObjectValue());
                }
            }
            SponsorBlockSettings.exportCategoriesToFlatJson(alertDialogContext, json);

            if (json.length() == 0) {
                return "";
            }
            String export = json.toString(0);
            // Remove the outer JSON braces to make the output more compact,
            // and leave less chance of the user forgetting to copy it
            return export.substring(2, export.length() - 2);
        } catch (JSONException e) {
            LogHelper.printException(() -> "Export failure", e); // should never happen
            return "";
        }
    }

    /**
     * @return if any settings that require a reboot were changed.
     */
    public static boolean importJSON(@NonNull String settingsJsonString) {
        try {
            if (!settingsJsonString.matches("[\\s\\S]*\\{")) {
                settingsJsonString = '{' + settingsJsonString + '}'; // Restore outer JSON braces
            }
            JSONObject json = new JSONObject(settingsJsonString);

            ReVancedSettingsFragment.settingImportInProgress = true;
            boolean rebootSettingChanged = false;
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
                        rebootSettingChanged |= setting.rebootApp;
                        setting.saveValue(value);
                    }
                    numberOfSettingsImported++;
                } else if (setting.includeWithImportExport() && setting.isNotSetToDefault()) {
                    LogHelper.printDebug(() -> "Resetting to default: " + setting);
                    rebootSettingChanged |= setting.rebootApp;
                    setting.resetToDefault();
                }
            }
            numberOfSettingsImported += SponsorBlockSettings.importCategoriesFromFlatJson(json);

            ReVancedUtils.showToastLong(numberOfSettingsImported == 0
                    ? str("revanced_extended_settings_import_reset")
                    : str("revanced_extended_settings_import_success"));

            return rebootSettingChanged;
        } catch (JSONException | IllegalArgumentException ex) {
            ReVancedUtils.showToastLong(str("revanced_extended_settings_import_failed", ex.getMessage()));
            LogHelper.printInfo(() -> "", ex);
        } catch (Exception ex) {
            LogHelper.printException(() -> "Import failure: " + ex.getMessage(), ex); // should never happen
        }
        return false;
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
