package app.revanced.integrations.youtube.patches.video;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.showToastShort;

import androidx.annotation.NonNull;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.patches.utils.PatchStatus;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;
import app.revanced.integrations.youtube.whitelist.Whitelist;

@SuppressWarnings("unused")
public class PlaybackSpeedPatch {
    private static boolean isLiveStream;

    /**
     * Injection point.
     */
    public static void newVideoStarted(@NonNull String newlyLoadedChannelId, @NonNull String newlyLoadedChannelName,
                                       @NonNull String newlyLoadedVideoId, @NonNull String newlyLoadedVideoTitle,
                                       final long newlyLoadedVideoLength, boolean newlyLoadedLiveStreamValue) {
        isLiveStream = newlyLoadedLiveStreamValue;
        Logger.printDebug(() -> "newVideoStarted: " + newlyLoadedVideoId);

        if (Settings.DISABLE_DEFAULT_PLAYBACK_SPEED_LIVE.get() && newlyLoadedLiveStreamValue)
            return;

        float defaultPlaybackSpeed = Settings.DEFAULT_PLAYBACK_SPEED.get();
        if (Whitelist.isChannelWhitelistedPlaybackSpeed(newlyLoadedChannelId))
            defaultPlaybackSpeed = 1.0f;

        VideoInformation.overridePlaybackSpeed(defaultPlaybackSpeed);
    }

    /**
     * Injection point.
     */
    public static float getPlaybackSpeedInShorts(final float playbackSpeed) {
        if (!VideoInformation.lastPlayerResponseIsShort())
            return playbackSpeed;
        if (!Settings.ENABLE_DEFAULT_PLAYBACK_SPEED_SHORTS.get())
            return playbackSpeed;
        if (Settings.DISABLE_DEFAULT_PLAYBACK_SPEED_LIVE.get() && isLiveStream)
            return playbackSpeed;

        float defaultPlaybackSpeed = Settings.DEFAULT_PLAYBACK_SPEED.get();
        if (Whitelist.isChannelWhitelistedPlaybackSpeed(VideoInformation.getChannelId()))
            defaultPlaybackSpeed = 1.0f;

        Logger.printDebug(() -> "getPlaybackSpeedInShorts: " + playbackSpeed);

        return defaultPlaybackSpeed;
    }

    /**
     * Injection point.
     * Called when user selects a playback speed.
     *
     * @param playbackSpeed The playback speed the user selected
     */
    public static void userSelectedPlaybackSpeed(float playbackSpeed) {
        if (!Settings.REMEMBER_PLAYBACK_SPEED_LAST_SELECTED.get())
            return;

        if (!PatchStatus.RememberPlaybackSpeed())
            return;

        Settings.DEFAULT_PLAYBACK_SPEED.save(playbackSpeed);
        showToastShort(str("revanced_remember_playback_speed_toast", playbackSpeed + "x"));
    }
}
