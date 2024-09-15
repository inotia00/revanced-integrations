package app.revanced.integrations.youtube.patches.components;

import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class AdsFilter extends Filter {

    public AdsFilter() {

        final StringFilterGroup alertBannerPromo = new StringFilterGroup(
                Settings.HIDE_PROMOTION_ALERT_BANNER,
                "alert_banner_promo.eml"
        );

        final StringFilterGroup carouselAd = new StringFilterGroup(
                Settings.HIDE_GENERAL_ADS,
                "carousel_ad"
        );

        final StringFilterGroup generalAds = new StringFilterGroup(
                Settings.HIDE_GENERAL_ADS,
                "ads_video_with_context",
                "banner_text_icon",
                "brand_video",
                "button_banner",
                "carousel_footered_layout",
                "carousel_headered_layout",
                "full_width_portrait_image_layout",
                "full_width_square_image_layout",
                "hero_promo_image",
                "landscape_image_wide_button_layout",
                "legal_disclosure",
                "lumiere_promo_carousel",
                "primetime_promo",
                "product_details",
                "square_image_layout",
                "statement_banner",
                "text_image_button_layout",
                "video_display_carousel_button",
                "video_display_full_layout",
                "watch_metadata_app_promo",
                "_ad_with",
                "_button_group_layout",
                "_buttoned_layout",
                "_image_layout"
        );

        addIdentifierCallbacks(
                alertBannerPromo,
                carouselAd,
                // In the new layout, filter strings are not included in the path, but instead in the identifier.
                generalAds
        );

        final StringFilterGroup merchandise = new StringFilterGroup(
                Settings.HIDE_MERCHANDISE_SHELF,
                "product_carousel",
                "shopping_carousel"
        );

        final StringFilterGroup paidContent = new StringFilterGroup(
                Settings.HIDE_PAID_PROMOTION_LABEL,
                "paid_content_overlay"
        );

        final StringFilterGroup selfSponsor = new StringFilterGroup(
                Settings.HIDE_SELF_SPONSOR_CARDS,
                "cta_shelf_card"
        );

        final StringFilterGroup viewProducts = new StringFilterGroup(
                Settings.HIDE_VIEW_PRODUCTS,
                "product_item",
                "products_in_video"
        );

        final StringFilterGroup webSearchPanel = new StringFilterGroup(
                Settings.HIDE_WEB_SEARCH_RESULTS,
                "web_link_panel",
                "web_result_panel"
        );

        addPathCallbacks(
                generalAds,
                merchandise,
                paidContent,
                selfSponsor,
                viewProducts,
                webSearchPanel
        );
    }
}
