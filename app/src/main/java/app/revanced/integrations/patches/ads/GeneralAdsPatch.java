package app.revanced.integrations.patches.ads;

import static app.revanced.integrations.patches.utils.PatchStatus.ShortsComponent;

import android.view.View;

import app.revanced.integrations.adremover.AdRemoverAPI;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedUtils;

public final class GeneralAdsPatch extends Filter {
    private final String[] IGNORE = {
            "comment_thread", // skip blocking anything in the comments
            "download_",
            "downloads_",
            "library_recent_shelf",
            "playlist_add_to_option_wrapper",
            "related_video_with_context",
            "|comment." // skip blocking anything in the comments replies
    };

    private final BlockRule custom = new CustomBlockRule(
            SettingsEnum.ADREMOVER_USER_FILTER,
            SettingsEnum.ADREMOVER_CUSTOM_FILTER
    );

    public GeneralAdsPatch() {
        var albumCard = new BlockRule(SettingsEnum.HIDE_ALBUM_CARDS, "official_card", "browsy_bar");
        var audioTrackButton = new BlockRule(SettingsEnum.HIDE_AUDIO_TRACK_BUTTON, "multi_feed_icon_button");
        var carouselAd = new BlockRule(SettingsEnum.ADREMOVER_GENERAL_ADS, "carousel_ad");
        var channelGuidelines = new BlockRule(SettingsEnum.HIDE_CHANNEL_GUIDELINES, "channel_guidelines_entry_banner", "community_guidelines", "sponsorships_comments_upsell");
        var channelMemberShelf = new BlockRule(SettingsEnum.HIDE_CHANNEL_MEMBER_SHELF, "member_recognition_shelf");
        var graySeparator = new BlockRule(SettingsEnum.HIDE_GRAY_SEPARATOR, "cell_divider");
        var imageShelf = new BlockRule(SettingsEnum.HIDE_IMAGE_SHELF, "image_shelf");
        var inFeedSurvey = new BlockRule(SettingsEnum.HIDE_FEED_SURVEY, "in_feed_survey", "slimline_survey");
        var infoPanel = new BlockRule(SettingsEnum.HIDE_INFO_PANEL, "compact_banner", "publisher_transparency_panel", "single_item_information_panel");
        var joinMembership = new BlockRule(SettingsEnum.HIDE_CHANNEL_BAR_JOIN_BUTTON, "compact_sponsor_button");
        var latestPosts = new BlockRule(SettingsEnum.HIDE_LATEST_POSTS, "post_shelf");
        var medicalPanel = new BlockRule(SettingsEnum.HIDE_MEDICAL_PANEL, "medical_panel", "emergency_onebox");
        var merchandise = new BlockRule(SettingsEnum.HIDE_MERCHANDISE, "product_carousel");
        var paidContent = new BlockRule(SettingsEnum.ADREMOVER_PAID_CONTENT, "paid_content_overlay");
        var selfSponsor = new BlockRule(SettingsEnum.ADREMOVER_SELF_SPONSOR, "cta_shelf_card");
        var teaser = new BlockRule(SettingsEnum.HIDE_TEASER, "expandable_metadata");
        var ticketShelf = new BlockRule(SettingsEnum.HIDE_TICKET_SHELF, "ticket_shelf");
        var timedReactions = new BlockRule(SettingsEnum.HIDE_TIMED_REACTIONS, "emoji_control_panel", "timed_reaction_player_animation", "timed_reaction_live_player_overlay");
        var viewProducts = new BlockRule(SettingsEnum.HIDE_VIEW_PRODUCTS, "product_item", "products_in_video");
        var webSearchPanel = new BlockRule(SettingsEnum.HIDE_WEB_SEARCH_PANEL, "web_link_panel");

        var buttonedAd = new BlockRule(SettingsEnum.ADREMOVER_BUTTON_ADS,
                "full_width_square_image_layout",
                "landscape_image_wide_button_layout",
                "video_display_button_group_layout",
                "video_display_carousel_buttoned_layout",
                "_ad_with",
                "_buttoned_layout"
        );
        var generalAds = new BlockRule(
            SettingsEnum.ADREMOVER_GENERAL_ADS,
                "active_view_display_container",
                "ads_",
                "ads_video_with_context",
                "ad_",
                "banner_text_icon",
                "brand_video_shelf",
                "brand_video_singleton",
                "carousel_footered_layout",
                "hero_promo_image",
                "legal_disclosure_cell",
                "primetime_promo",
                "product_details",
                "square_image_layout",
                "statement_banner",
                "text_image_button_layout",
                "video_display_full_layout",
                "watch_metadata_app_promo",
                "_ad",
                "_ads",
                "|ads_",
                "|ad_"
        );
        var movieAds = new BlockRule(
                SettingsEnum.HIDE_MOVIE_SHELF,
                "compact_movie",
                "horizontal_movie_shelf",
                "movie_and_show_upsell_card",
                "compact_tvfilm_item",
                "offer_module_root"
        );
        var shorts = new BlockRule(SettingsEnum.HIDE_SHORTS_SHELF,
                "reels_player_overlay",
                "shorts_shelf",
                "inline_shorts",
                "shorts_grid"
        );

        this.pathRegister.registerAll(
                albumCard,
                audioTrackButton,
                buttonedAd,
                channelGuidelines,
                channelMemberShelf,
                generalAds,
                imageShelf,
                inFeedSurvey,
                infoPanel,
                joinMembership,
                latestPosts,
                medicalPanel,
                merchandise,
                movieAds,
                paidContent,
                selfSponsor,
                teaser,
                ticketShelf,
                timedReactions,
                viewProducts,
                webSearchPanel
        );
        this.identifierRegister.registerAll(carouselAd, graySeparator);

        if (ShortsComponent())
            this.identifierRegister.registerAll(shorts);
    }

    public boolean filter(final String path, final String identifier) {
        BlockResult result;

        if (custom.isEnabled() && custom.check(path).isBlocked())
            result = BlockResult.CUSTOM;
        else if (ReVancedUtils.containsAny(path, IGNORE))
            result = BlockResult.IGNORED;
        else if (pathRegister.contains(path) || identifierRegister.contains(identifier))
            result = BlockResult.DEFINED;
        else
            result = BlockResult.UNBLOCKED;

        return result.filter;
    }

    private enum BlockResult {
        UNBLOCKED(false, "Unblocked"),
        IGNORED(false, "Ignored"),
        DEFINED(true, "Blocked"),
        CUSTOM(true, "Custom");

        final boolean filter;
        final String message;

        BlockResult(boolean filter, String message) {
            this.filter = filter;
            this.message = message;
        }
    }

    /**
     * Hide the specific view, which shows ads in the homepage.
     *
     * @param view The view, which shows ads.
     */
    public static void hideAdAttributionView(View view) {
        if (!SettingsEnum.ADREMOVER_GENERAL_ADS.getBoolean()) return;
        AdRemoverAPI.HideViewWithLayout1dp(view);
    }

    public static void hideBreakingNewsShelf(View view) {
        if (!SettingsEnum.HIDE_BREAKING_NEWS_SHELF.getBoolean()) return;
        AdRemoverAPI.HideViewWithLayout1dp(view);
    }
}
