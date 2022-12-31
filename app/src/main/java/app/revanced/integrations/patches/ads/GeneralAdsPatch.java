package app.revanced.integrations.patches.ads;

import android.view.View;

import app.revanced.integrations.adremover.AdRemoverAPI;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedUtils;

public final class GeneralAdsPatch extends Filter {
    private final String[] IGNORE = {
            "|comment.",
            "comment_thread", // skip blocking anything in the comments
            "home_video_with_context",
            "related_video_with_context",
            "library_recent_shelf",
            "playlist_add_to_option_wrapper" // do not block on "add to playlist" flyout menu
    };

    private final BlockRule custom = new CustomBlockRule(
            SettingsEnum.ADREMOVER_USER_FILTER,
            SettingsEnum.ADREMOVER_CUSTOM_FILTER
    );

    public GeneralAdsPatch() {
        var channelMemberShelf = new BlockRule(SettingsEnum.ADREMOVER_CHANNEL_MEMBER_SHELF_REMOVAL, "member_recognition_shelf");
        var communityPosts = new BlockRule(SettingsEnum.ADREMOVER_COMMUNITY_POSTS, "post_base_wrapper");
        var communityGuidelines = new BlockRule(SettingsEnum.ADREMOVER_COMMUNITY_GUIDELINES, "community_guidelines");
        var subscribersCommunityGuidelines = new BlockRule(SettingsEnum.ADREMOVER_SUBSCRIBERS_COMMUNITY_GUIDELINES, "sponsorships_comments_upsell");
        var compactBanner = new BlockRule(SettingsEnum.ADREMOVER_COMPACT_BANNER, "compact_banner");
        var inFeedSurvey = new BlockRule(SettingsEnum.ADREMOVER_FEED_SURVEY, "infeed_survey");
        var medicalPanel = new BlockRule(SettingsEnum.ADREMOVER_MEDICAL_PANEL, "medical_panel", "emergency_onebox");
        var paidContent = new BlockRule(SettingsEnum.ADREMOVER_PAID_CONTENT, "paid_content_overlay");
        var merchandise = new BlockRule(SettingsEnum.ADREMOVER_MERCHANDISE, "product_carousel");
        var imageShelf = new BlockRule(SettingsEnum.ADREMOVER_IMAGE_SHELF, "image_shelf");
        var infoPanel = new BlockRule(SettingsEnum.ADREMOVER_INFO_PANEL, "publisher_transparency_panel", "single_item_information_panel");
        var suggestions = new BlockRule(SettingsEnum.ADREMOVER_SUGGESTIONS, "horizontal_video_shelf");
        var latestPosts = new BlockRule(SettingsEnum.ADREMOVER_LATEST_POSTS, "post_shelf");
        var channelGuidelines = new BlockRule(SettingsEnum.ADREMOVER_CHANNEL_GUIDELINES, "channel_guidelines_entry_banner");
        var officialCard = new BlockRule(SettingsEnum.ADREMOVER_OFFICIAL_CARDS, "official_card");
        var selfSponsor = new BlockRule(SettingsEnum.ADREMOVER_SELF_SPONSOR, "cta_shelf_card");
        var joinMembership = new BlockRule(SettingsEnum.ADREMOVER_CHANNELBAR_JOIN_BUTTON, "compact_sponsor_button");
        var chapterTeaser = new BlockRule(SettingsEnum.ADREMOVER_CHAPTER_TEASER_REMOVAL, "expandable_metadata");
        var graySeparator = new BlockRule(SettingsEnum.ADREMOVER_GRAY_SEPARATOR,
                "cell_divider",
                "member_recognition_shelf"
        );
        var buttonedAd = new BlockRule(SettingsEnum.ADREMOVER_BUTTON_ADS,
                "video_display_full_buttoned_layout",
                "full_width_square_image_layout",
                "_ad",
                "landscape_image_wide_button_layout"
        );
        var generalAds = new BlockRule(
            SettingsEnum.ADREMOVER_GENERAL_ADS,
                "video_display_full_layout",
                "active_view_display_container",
                "|ad_",
                "|ads_",
                "_ad_with",
                "ads_video_with_context",
                "banner_text_icon",	
                "legal_disclosure_cell",
                "primetime_promo",
                "brand_video_shelf",
                "statement_banner",
                "square_image_layout",
                "watch_metadata_app_promo",
                "video_display_full_layout"	
        );
        var movieAds = new BlockRule(
                SettingsEnum.ADREMOVER_MOVIE_SHELF,
                "browsy_bar",
                "compact_movie",
                "horizontal_movie_shelf",
                "movie_and_show_upsell_card",
                "compact_tvfilm_item"
        );

        this.pathRegister.registerAll(
                generalAds,
                buttonedAd,
                communityPosts,
                paidContent,
                imageShelf,
                suggestions,
                latestPosts,
                movieAds,
                communityGuidelines,
                compactBanner,
                inFeedSurvey,
                medicalPanel,
                merchandise,
                infoPanel,
                channelGuidelines,
                officialCard,
                selfSponsor,
                joinMembership,
                chapterTeaser,
                artistCard,	
                subscribersCommunityGuidelines,
                channelMemberShelf
        );

        var carouselAd = new BlockRule(SettingsEnum.ADREMOVER_GENERAL_ADS,
                "carousel_ad");	
        var shorts = new BlockRule(SettingsEnum.ADREMOVER_SHORTS_REMOVAL,	
                "reels_player_overlay",	
                "shorts_shelf",	
                "inline_shorts",	
                "shorts_grid"
        );

        this.identifierRegister.registerAll(
                shorts,	
                graySeparator,
                carouselAd
        );
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

        final Boolean filter;
        final String message;

        BlockResult(boolean filter, String message) {
            this.filter = filter;
            this.message = message;
        }
    }
    /**
     * Hide a view.	
     *	
     * @param condition The setting to check for hiding the view.	
     * @param view      The view to hide.	
     */	
    private static void hideView(SettingsEnum condition, View view) {	
        if (!condition.getBoolean()) return;	
        log("Hiding view with setting: " + condition);	
        AdRemoverAPI.HideViewWithLayout1dp(view);	
    }	
    /**	
     * Hide the view, which shows ads in the homepage.
     *
     * @param view The view, which shows ads.
     */
    public static void hideAdAttributionView(View view) {
        if (!SettingsEnum.ADREMOVER_GENERAL_ADS.getBoolean()) return;
        AdRemoverAPI.HideViewWithLayout1dp(view);
    }

    public static void hideBreakingNewsShelf(View view) {
        if (!SettingsEnum.ADREMOVER_BREAKING_NEWS_SHELF.getBoolean()) return;
        AdRemoverAPI.HideViewWithLayout1dp(view);
    }

    public static void hideAlbumCards(View view) {
        if (!SettingsEnum.ADREMOVER_ALBUM_CARDS.getBoolean()) return;
        AdRemoverAPI.HideViewWithLayout1dp(view);
    }
    
     /**	
     * Hide the view, which shows reels in the homepage.	
     *	
     * @param view The view, which shows reels.	
     */	
    public static void hideReelView(View view) {	
        hideView(SettingsEnum.ADREMOVER_SHORTS_REMOVAL, view);	
    }
}
