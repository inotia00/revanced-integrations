package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;

public final class ShortsFilter extends Filter {
    private static final String REEL_CHANNEL_BAR_PATH = "reel_channel_bar.eml";

    private final StringFilterGroup infoPanel;
    private final StringFilterGroup shelfHeader;

    private final StringFilterGroup videoActionButton;
    private final ByteArrayFilterGroupList videoActionButtonGroupList = new ByteArrayFilterGroupList();


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
                "shorts_video_cell"
        );

        shelfHeader = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_SHELF,
                "shelf_header.eml"
        );

        identifierFilterGroupList.addAll(shorts, shelfHeader, thanksButton);

        final var joinButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_JOIN_BUTTON,
                "sponsor_button"
        );

        final var subscribeButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_SUBSCRIPTIONS_BUTTON,
                "shorts_paused_state",
                "subscribe_button"
        );

        infoPanel = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_INFO_PANEL,
                "reel_multi_format_link",
                "reel_sound_metadata",
                "shorts_info_panel_overview"
        );

        videoActionButton = new StringFilterGroup(
                null,
                "ContainerType|shorts_video_action_button"
        );

        pathFilterGroupList.addAll(joinButton, subscribeButton, infoPanel, videoActionButton);

        final var shortsCommentButton = new ByteArrayAsStringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_COMMENTS_BUTTON,
                "reel_comment_button"
        );

        final var shortsRemixButton = new ByteArrayAsStringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_REMIX_BUTTON,
                "reel_remix_button"
        );

        final var shortsShareButton = new ByteArrayAsStringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_SHARE_BUTTON,
                "reel_share_button"
        );

        videoActionButtonGroupList.addAll(shortsCommentButton, shortsRemixButton, shortsShareButton);
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (matchedList == pathFilterGroupList) {
            // Always filter if matched.
            if (matchedGroup == infoPanel)
                return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);

            // Video action buttons have the same path.
            if (matchedGroup == videoActionButton) {
                if (videoActionButtonGroupList.check(protobufBufferArray).isFiltered())
                    return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
                return false;
            }

            // Filter other path groups from pathFilterGroupList, only when reelChannelBar is visible
            // to avoid false positives.
            if (!path.startsWith(REEL_CHANNEL_BAR_PATH))
                return false;
        } else if (matchedGroup == shelfHeader) {
            // Because the header is used in watch history and possibly other places, check for the index,
            // which is 0 when the shelf header is used for Shorts.
            if (matchedIndex != 0)
                return false;
        }

        // Super class handles logging.
        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
    }
}
