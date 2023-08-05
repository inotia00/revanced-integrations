package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;

public final class ShortsFilter extends Filter {

    public ShortsFilter() {
        final var thanksButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_THANKS_BUTTON,
                "suggested_action"
        );

        final var subscribeButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_SUBSCRIPTIONS_BUTTON,
                "subscribe_button"
        );

        final var joinButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_JOIN_BUTTON,
                "sponsor_button"
        );

        final var pivotButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_PIVOT_BUTTON,
                "reel_pivot_button"
        );

        final var shorts = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_SHELF,
                "shorts_shelf",
                "inline_shorts",
                "shorts_grid",
                "shorts_video_cell",
                "shorts_pivot_item"
        );
        //pivotButton must be added into 2 groups (I don't know why)
        this.pathFilterGroups.addAll(joinButton, subscribeButton, pivotButton);
        this.identifierFilterGroups.addAll(shorts, thanksButton, pivotButton);
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (pathFilterGroups == matchedList) {
            return path.contains("reel_channel_bar");
        }

        return identifierFilterGroups == matchedList;
    }
}
