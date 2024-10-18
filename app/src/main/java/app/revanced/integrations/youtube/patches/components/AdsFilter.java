package app.revanced.integrations.youtube.patches.components;

import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.youtube.settings.Settings;

/**
 * If A/B testing is applied, ad components can only be filtered by identifier
 * <p>
 * Before A/B testing:
 * Identifier: video_display_button_group_layout.eml
 * Path: video_display_button_group_layout.eml|ContainerType|....
 * (Path always starts with an Identifier)
 * <p>
 * After A/B testing:
 * Identifier: video_display_button_group_layout.eml
 * Path: video_lockup_with_attachment.eml|ContainerType|....
 * (Path does not contain an Identifier)
 */
@SuppressWarnings("unused")
public final class AdsFilter extends Filter {

    public AdsFilter() {

        // Identifiers.

        final StringFilterGroup alertBannerPromo = new StringFilterGroup(
                Settings.HIDE_PROMOTION_ALERT_BANNER,
                "alert_banner_promo.eml"
        );

        // Keywords checked in 2024:
        final StringFilterGroup generalAdsIdentifier = new StringFilterGroup(
                Settings.HIDE_GENERAL_ADS,
                // "brand_video_shelf.eml"
                "brand_video",

                // "carousel_footered_layout.eml"
                "carousel_footered_layout",

                // "composite_concurrent_carousel_layout"
                "composite_concurrent_carousel_layout",

                // "landscape_image_wide_button_layout.eml"
                "landscape_image_wide_button_layout",

                // "square_image_layout.eml"
                "square_image_layout",

                // "statement_banner.eml"
                "statement_banner",

                // "video_display_full_layout.eml"
                "video_display_full_layout",

                // "text_image_button_group_layout.eml"
                // "video_display_button_group_layout.eml"
                "_button_group_layout",

                // "banner_text_icon_buttoned_layout.eml"
                // "video_display_compact_buttoned_layout.eml"
                // "video_display_full_buttoned_layout.eml"
                "_buttoned_layout",

                // "compact_landscape_image_layout.eml"
                // "full_width_portrait_image_layout.eml"
                // "full_width_square_image_layout.eml"
                "_image_layout"
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

        addIdentifierCallbacks(
                alertBannerPromo,
                generalAdsIdentifier,
                merchandise,
                paidContent,
                selfSponsor,
                viewProducts,
                webSearchPanel
        );

        // Path.

        final StringFilterGroup generalAdsPath = new StringFilterGroup(
                Settings.HIDE_GENERAL_ADS,
                "carousel_ad",
                "carousel_headered_layout",
                "hero_promo_image",
                "legal_disclosure",
                "lumiere_promo_carousel",
                "primetime_promo",
                "product_details",
                "text_image_button_layout",
                "video_display_carousel_button",
                "watch_metadata_app_promo"
        );

        addPathCallbacks(
                generalAdsPath,
                viewProducts
        );
    }
}
