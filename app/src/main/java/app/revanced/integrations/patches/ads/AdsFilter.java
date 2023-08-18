package app.revanced.integrations.patches.ads;

import android.view.View;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.utils.StringTrieSearch;

public final class AdsFilter extends Filter {
    private final StringTrieSearch exceptions = new StringTrieSearch();


    public AdsFilter() {
        exceptions.addPatterns(
                "comment_thread", // skip blocking anything in the comments
                "download_",
                "downloads_",
                "home_video_with_context", // Don't filter anything in the home page video component.
                "library_recent_shelf",
                "playlist_add",
                "related_video_with_context", // Don't filter anything in the related video component.
                "|comment." // skip blocking anything in the comments replies
        );

        final var carouselAd = new StringFilterGroup(
                SettingsEnum.HIDE_GENERAL_ADS,
                "carousel_ad"
        );

        final var imageShelf = new StringFilterGroup(
                SettingsEnum.HIDE_IMAGE_SHELF,
                "image_shelf"
        );

        final var merchandise = new StringFilterGroup(
                SettingsEnum.HIDE_MERCHANDISE_SHELF,
                "expandable_list_inner",
                "product_carousel"
        );

        final var paidContent = new StringFilterGroup(
                SettingsEnum.HIDE_PAID_PROMOTION,
                "paid_content_overlay"
        );

        final var selfSponsor = new StringFilterGroup(
                SettingsEnum.HIDE_SELF_SPONSOR_CARDS,
                "cta_shelf_card"
        );

        final var generalAds = new StringFilterGroup(
                SettingsEnum.HIDE_GENERAL_ADS,
                "active_view_display_container",
                "ads_",
                "ads_video_with_context",
                "ad_",
                "banner_text_icon",
                "brand_video_shelf",
                "brand_video_singleton",
                "carousel_footered_layout",
                "carousel_headered_layout",
                "full_width_square_image_layout",
                "hero_promo_image",
                "landscape_image_wide_button_layout",
                "legal_disclosure_cell",
                "lumiere_promo_carousel",
                "primetime_promo",
                "product_details",
                "square_image_layout",
                "statement_banner",
                "text_image_button_group_layout",
                "text_image_button_layout",
                "video_display_button_group_layout",
                "video_display_carousel_buttoned_layout",
                "video_display_full_layout",
                "watch_metadata_app_promo",
                "_ad",
                "_ads",
                "_ad_with",
                "_buttoned_layout",
                "|ads_",
                "|ad_"
        );

        final var viewProducts = new StringFilterGroup(
                SettingsEnum.HIDE_VIEW_PRODUCTS,
                "expandable_product_grid",
                "product_item",
                "products_in_video",
                "expandable_product_grid"
        );

        final var webSearchPanel = new StringFilterGroup(
                SettingsEnum.HIDE_WEB_SEARCH_RESULTS,
                "web_link_panel"
        );

        this.pathFilterGroups.addAll(
                generalAds,
                imageShelf,
                merchandise,
                paidContent,
                selfSponsor,
                viewProducts,
                webSearchPanel
        );

        this.identifierFilterGroups.addAll(
                carouselAd
        );
    }

    /**
     * Hide the view, which shows ads in the homepage.
     *
     * @param view The view, which shows ads.
     */
    public static void hideAdAttributionView(View view) {
        ReVancedUtils.hideViewBy0dpUnderCondition(SettingsEnum.HIDE_GENERAL_ADS.getBoolean(), view);
    }

    public static boolean hideGetPremium() {
        return SettingsEnum.HIDE_GET_PREMIUM.getBoolean();
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (exceptions.matches(path))
            return false;

        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
    }
}
