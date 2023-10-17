package app.revanced.integrations.patches.ads;

import app.revanced.integrations.settings.SettingsEnum;

final class WaterMarkFilter extends Filter {

    public WaterMarkFilter() {
        final var waterMark = new StringFilterGroup(
                SettingsEnum.HIDE_CHANNEL_WATERMARK,
                "featured_channel_watermark_overlay.eml"
        );

        this.pathFilterGroupList.addAll(
                waterMark
        );
    }
}
