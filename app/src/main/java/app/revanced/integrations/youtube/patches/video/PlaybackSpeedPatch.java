package app.revanced.integrations.youtube.patches.video;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.showToastShort;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.BooleanUtils;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.patches.misc.requests.PlaylistRequest;
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

        final float defaultPlaybackSpeed = getDefaultPlaybackSpeed(newlyLoadedChannelId, newlyLoadedVideoId);
        Logger.printDebug(() -> "overridePlaybackSpeed: " + defaultPlaybackSpeed);

        VideoInformation.overridePlaybackSpeed(defaultPlaybackSpeed);
    }

    /**
     * Injection point.
     */
    public static void fetchPlaylistData(@NonNull String videoId, boolean isShortAndOpeningOrPlaying) {
        if (Settings.DISABLE_DEFAULT_PLAYBACK_SPEED_MUSIC.get()) {
            try {
                final boolean videoIdIsShort = VideoInformation.lastPlayerResponseIsShort();
                // Shorts shelf in home and subscription feed causes player response hook to be called,
                // and the 'is opening/playing' parameter will be false.
                // This hook will be called again when the Short is actually opened.
                if (videoIdIsShort && !isShortAndOpeningOrPlaying) {
                    return;
                }

                PlaylistRequest.fetchRequestIfNeeded(videoId);
            } catch (Exception ex) {
                Logger.printException(() -> "fetchPlaylistData failure", ex);
            }
        }
    }

    /**
     * Injection point.
     */
    public static float getPlaybackSpeedInShorts(final float playbackSpeed) {
        if (!VideoInformation.lastPlayerResponseIsShort())
            return playbackSpeed;
        if (!Settings.ENABLE_DEFAULT_PLAYBACK_SPEED_SHORTS.get())
            return playbackSpeed;

        float defaultPlaybackSpeed = getDefaultPlaybackSpeed(VideoInformation.getChannelId(), null);
        Logger.printDebug(() -> "overridePlaybackSpeed in Shorts: " + defaultPlaybackSpeed);

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

        if (!Settings.REMEMBER_PLAYBACK_SPEED_LAST_SELECTED_TOAST.get())
            return;

        showToastShort(str("revanced_remember_playback_speed_toast", playbackSpeed + "x"));
    }

    private static float getDefaultPlaybackSpeed(@NonNull String channelId, @Nullable String videoId) {
        return (Settings.DISABLE_DEFAULT_PLAYBACK_SPEED_LIVE.get() && isLiveStream) ||
                Whitelist.isChannelWhitelistedPlaybackSpeed(channelId) ||
                getPlaylistData(videoId)
                ? 1.0f
                : Settings.DEFAULT_PLAYBACK_SPEED.get();
    }

    private static boolean getPlaylistData(@Nullable String videoId) {
        if (Settings.DISABLE_DEFAULT_PLAYBACK_SPEED_MUSIC.get() && videoId != null) {
            try {
                PlaylistRequest request = PlaylistRequest.getRequestForVideoId(videoId);
                final boolean isPlaylist = request != null && BooleanUtils.toBoolean(request.getStream());
                Logger.printDebug(() -> "isPlaylist: " + isPlaylist);

                return isPlaylist;
            } catch (Exception ex) {
                Logger.printException(() -> "getPlaylistData failure", ex);
            }
        }

        return false;
    }
}
