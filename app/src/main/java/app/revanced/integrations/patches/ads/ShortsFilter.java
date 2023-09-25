package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;

public final class ShortsFilter extends Filter {
    private static final String REEL_CHANNEL_BAR_PATH = "reel_channel_bar.eml";

    private final StringFilterGroup shortsShelfHeader;

    public ShortsFilter() {
        final var thanksButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_THANKS_BUTTON,
                "suggested_action"
        );

        final var shorts = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_SHELF,
                "shorts_shelf",
                "inline_shorts",
                "shorts_grid",
                "shorts_video_cell",
                "shorts_pivot_item"
        );

        shortsShelfHeader = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_SHELF,
                "shelf_header.eml"
        );

        final var joinButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_JOIN_BUTTON,
                "sponsor_button"
        );

        final var subscribeButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_SUBSCRIPTIONS_BUTTON,
                "subscribe_button"
        );

        final var pivotButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_PIVOT_BUTTON,
                "reel_pivot_button"
        );

        identifierFilterGroups.addAll(shorts, shortsShelfHeader, thanksButton, pivotButton);
        pathFilterGroups.addAll(joinButton, subscribeButton, pivotButton);
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (matchedList == pathFilterGroups) {
            // Filter other path groups from pathFilterGroups, only when reelChannelBar is visible
            // to avoid false positives.
            if (!path.startsWith(REEL_CHANNEL_BAR_PATH))
                return false;
        } else if (matchedGroup == shortsShelfHeader) {
            // Because the header is used in watch history and possibly other places, check for the index,
            // which is 0 when the shelf header is used for Shorts.
            if (matchedIndex != 0) return false;
        }

        // Super class handles logging.
        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
    }
}
