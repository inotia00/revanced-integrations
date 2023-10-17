package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.shared.PlayerType;

final class PlayerFlyoutPanelsFilter extends Filter {
    // Search the buffer only if the flyout menu identifier is found.
    // Handle the searching in this class instead of adding to the global filter group (which searches all the time)
    private final ByteArrayFilterGroupList flyoutFilterGroupList = new ByteArrayFilterGroupList();

    public PlayerFlyoutPanelsFilter() {
        identifierFilterGroupList.addAll(new StringFilterGroup(null, "overflow_menu_item.eml|"));

        flyoutFilterGroupList.addAll(
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_AMBIENT,
                        "yt_outline_screen_light"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_AUDIO_TRACK,
                        "yt_outline_person_radar"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_CAPTIONS,
                        "closed_caption"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_HELP,
                        "yt_outline_question_circle"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_LISTENING_CONTROLS,
                        "yt_outline_adjust"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_LOCK_SCREEN,
                        "yt_outline_lock"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_LOOP,
                        "yt_outline_arrow_repeat_1_"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_MORE,
                        "yt_outline_info_circle"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_PLAYBACK_SPEED,
                        "yt_outline_play_arrow_half_circle"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_QUALITY,
                        "yt_outline_gear"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_REPORT,
                        "yt_outline_flag"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_STABLE_VOLUME,
                        "volume_stable"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_STATS_FOR_NERDS,
                        "yt_outline_statistics_graph"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_WATCH_IN_VR,
                        "yt_outline_vr"
                ),
                new ByteArrayAsStringFilterGroup(
                        SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_YT_MUSIC,
                        "yt_outline_open_new"
                )
        );
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        // In YouTube v18.33.xx+, Shorts also use the player flyout panel
        if (PlayerType.getCurrent().isNoneOrHidden())
            return false;
        // Only 1 group is added to the parent class, so the matched group must be the overflow menu.
        if (matchedIndex == 0 && flyoutFilterGroupList.check(protobufBufferArray).isFiltered()) {
            // Super class handles logging.
            return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);
        }
        return false;
    }
}
