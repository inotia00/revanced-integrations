package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;

final class ButtonsFilter extends Filter {

    private static final String VIDEO_ACTION_BAR_PATH = "video_action_bar.eml";

    private final StringFilterGroup actionBarRule;
    private final StringFilterGroup bufferFilterPathRule;
    private final ByteArrayFilterGroupList bufferButtonsGroupList = new ByteArrayFilterGroupList();

    public ButtonsFilter() {
        actionBarRule = new StringFilterGroup(
                null,
                VIDEO_ACTION_BAR_PATH
        );
        identifierFilterGroups.addAll(actionBarRule);


        bufferFilterPathRule = new StringFilterGroup(
                null,
                "|CellType|CollectionType|CellType|ContainerType|button.eml|"
        );
        pathFilterGroups.addAll(
                new StringFilterGroup(
                        SettingsEnum.HIDE_LIKE_BUTTON,
                        "|CellType|ContainerType|like_button",
                        "|CellType|ContainerType|segmented_like_dislike_button"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_DISLIKE_BUTTON,
                        "|CellType|ContainerType|dislike_button",
                        "|CellType|ContainerType|segmented_like_dislike_button"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_DOWNLOAD_BUTTON,
                        "|download_button.eml|"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_CREATE_CLIP_BUTTON,
                        "|clip_button.eml|"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_PLAYLIST_BUTTON,
                        "|save_to_playlist_button"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_REWARDS_BUTTON,
                        "account_link_button"
                ),
                new StringFilterGroup(
                        SettingsEnum.HIDE_ACTION_BUTTON,
                        "|video_action_button.eml|"
                ),
                bufferFilterPathRule
        );

        bufferButtonsGroupList.addAll(
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_LIVE_CHAT_BUTTON,
                        "yt_outline_message_bubble_overlap"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_REPORT_BUTTON,
                        "yt_outline_flag"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_SHARE_BUTTON,
                        "yt_outline_share"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_REMIX_BUTTON,
                        "yt_outline_youtube_shorts_plus"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_SHOP_BUTTON,
                        "yt_outline_bag"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_THANKS_BUTTON,
                        "yt_outline_dollar_sign_heart"
                )
        );
    }

    private boolean isEveryFilterGroupEnabled() {
        for (FilterGroup rule : pathFilterGroups)
            if (!rule.isEnabled()) return false;

        for (FilterGroup rule : bufferButtonsGroupList)
            if (!rule.isEnabled()) return false;

        return true;
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (matchedGroup == actionBarRule && !isEveryFilterGroupEnabled()) {
            return false;
        }
        if (matchedGroup == bufferFilterPathRule) {
            if (!path.startsWith(VIDEO_ACTION_BAR_PATH)) {
                return false; // Some other unknown button and not part of the player action buttons.
            }
            if (!bufferButtonsGroupList.check(protobufBufferArray).isFiltered()) {
                return false; // Action button is not set to hide.
            }
        }

        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
    }
}
