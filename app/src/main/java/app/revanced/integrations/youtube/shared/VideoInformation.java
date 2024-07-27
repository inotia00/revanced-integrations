package app.revanced.integrations.youtube.shared;

import static app.revanced.integrations.shared.utils.ResourceUtils.getString;
import static app.revanced.integrations.youtube.utils.VideoUtils.getFormattedTimeStamp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.utils.AlwaysRepeatPatch;

/**
 * Hooking class for the current playing video.
 *
 * @noinspection ALL
 */
public final class VideoInformation {
    private static final float DEFAULT_YOUTUBE_PLAYBACK_SPEED = 1.0f;
    private static final int DEFAULT_YOUTUBE_VIDEO_QUALITY = -2;
    private static final String DEFAULT_YOUTUBE_VIDEO_QUALITY_STRING = getString("quality_auto");
    /**
     * Prefix present in all Short player parameters signature.
     */
    private static final String SHORTS_PLAYER_PARAMETERS = "8AEB";

    @NonNull
    private static String channelId = "";
    @NonNull
    private static String channelName = "";
    @NonNull
    private static String videoId = "";
    @NonNull
    private static String videoTitle = "";
    private static long videoLength = 0;
    private static boolean videoIsLiveStream;
    private static long videoTime = -1;

    @NonNull
    private static volatile String playerResponseVideoId = "";
    private static volatile boolean playerResponseVideoIdIsShort;
    private static volatile boolean videoIdIsShort;

    /**
     * The current playback speed
     */
    private static float playbackSpeed = DEFAULT_YOUTUBE_PLAYBACK_SPEED;
    /**
     * The current video quality
     */
    private static int videoQuality = DEFAULT_YOUTUBE_VIDEO_QUALITY;
    /**
     * The current video quality string
     */
    private static String videoQualityString = DEFAULT_YOUTUBE_VIDEO_QUALITY_STRING;
    /**
     * The available qualities of the current video in human readable form: [1080, 720, 480]
     */
    @Nullable
    private static List<Integer> videoQualities;

    /**
     * Injection point.
     */
    public static void initialize(@NonNull Object ignoredPlayerController) {
        videoTime = -1;
        videoLength = 0;
        playbackSpeed = DEFAULT_YOUTUBE_PLAYBACK_SPEED;
        Logger.printDebug(() -> "Initialized Player");
    }

    /**
     * Injection point.
     */
    public static void initializeMdx(@NonNull Object ignoredMdxPlayerDirector) {
        Logger.printDebug(() -> "Initialized Mdx Player");
    }

    public static boolean seekTo(final long seekTime) {
        return seekTo(seekTime, getVideoLength());
    }

    /**
     * Seek on the current video.
     * Does not function for playback of Shorts.
     * <p>
     * Caution: If called from a videoTimeHook() callback,
     * this will cause a recursive call into the same videoTimeHook() callback.
     *
     * @param seekTime The seekTime to seek the video to.
     * @return true if the seek was successful.
     */
    public static boolean seekTo(final long seekTime, final long videoLength) {
        Utils.verifyOnMainThread();
        try {
            final long videoTime = getVideoTime();
            final long adjustedSeekTime = getAdjustedSeekTime(seekTime, videoLength);

            Logger.printDebug(() -> "Seeking to " + getFormattedTimeStamp(adjustedSeekTime));
            try {
                if (overrideVideoTime(adjustedSeekTime)) {
                    return true;
                } // Else the video is loading or changing videos, or video is casting to a different device.
            } catch (Exception ex) {
                Logger.printInfo(() -> "seekTo method call failed", ex);
            }

            // Try calling the seekTo method of the MDX player director (called when casting).
            // The difference has to be a different second mark in order to avoid infinite skip loops
            // as the Lounge API only supports seconds.
            if ((adjustedSeekTime / 1000) == (videoTime / 1000)) {
                Logger.printDebug(() -> "Skipping seekTo for MDX because seek time is too small ("
                        + (adjustedSeekTime - videoTime) + "ms)");
                return false;
            }
            try {
                return overrideMDXVideoTime(adjustedSeekTime);
            } catch (Exception ex) {
                Logger.printInfo(() -> "seekTo (MDX) method call failed", ex);
                return false;
            }
        } catch (Exception ex) {
            Logger.printException(() -> "Failed to seek", ex);
            return false;
        }
    }

    // Prevent issues such as play/pause button or autoplay not working.
    private static long getAdjustedSeekTime(final long seekTime, final long videoLength) {
        // If the user skips to a section that is 500 ms before the video length,
        // it will get stuck in a loop.
        if (videoLength - seekTime > 500) {
            return seekTime;
        }

        // Both the current video time and the seekTo are in the last 500ms of the video.
        if (AlwaysRepeatPatch.alwaysRepeatEnabled()) {
            // If always-repeat is turned on, just skips to time 0.
            return 0;
        } else {
            // Otherwise, just skips to a time longer than the video length.
            // Paradoxically, if user skips to a section much longer than the video length, does not get stuck in a loop.
            return Integer.MAX_VALUE;
        }
    }

    @Deprecated
    public static void seekToRelative(long millisecondsRelative) {
        seekToRelative(millisecondsRelative, getVideoLength());
    }

    public static void seekToRelative(long millisecondsRelative, final long videoLength) {
        seekTo(videoTime + millisecondsRelative, videoLength);
    }

    /**
     * Injection point.
     *
     * @param newlyLoadedChannelId       id of the current channel.
     * @param newlyLoadedChannelName     name of the current channel.
     * @param newlyLoadedVideoId         id of the current video.
     * @param newlyLoadedVideoTitle      title of the current video.
     * @param newlyLoadedVideoLength     length of the video in milliseconds.
     * @param newlyLoadedLiveStreamValue whether the current video is a livestream.
     */
    public static void setVideoInformation(@NonNull String newlyLoadedChannelId, @NonNull String newlyLoadedChannelName,
                                           @NonNull String newlyLoadedVideoId, @NonNull String newlyLoadedVideoTitle,
                                           final long newlyLoadedVideoLength, boolean newlyLoadedLiveStreamValue) {
        if (videoId.equals(newlyLoadedVideoId))
            return;

        channelId = newlyLoadedChannelId;
        channelName = newlyLoadedChannelName;
        videoId = newlyLoadedVideoId;
        videoTitle = newlyLoadedVideoTitle;
        videoLength = newlyLoadedVideoLength;
        videoIsLiveStream = newlyLoadedLiveStreamValue;

        Logger.printDebug(() ->
                "channelId='" +
                        newlyLoadedChannelId +
                        "'\nchannelName='" +
                        newlyLoadedChannelName +
                        "'\nvideoId='" +
                        newlyLoadedVideoId +
                        "'\nvideoTitle='" +
                        newlyLoadedVideoTitle +
                        "'\nvideoLength=" +
                        getFormattedTimeStamp(newlyLoadedVideoLength) +
                        "videoIsLiveStream='" +
                        newlyLoadedLiveStreamValue +
                        "'"
        );
    }

    /**
     * Injection point.
     *
     * @param newlyLoadedVideoId id of the current video
     */
    public static void setVideoId(@NonNull String newlyLoadedVideoId) {
        if (videoId.equals(newlyLoadedVideoId))
            return;

        videoId = newlyLoadedVideoId;
    }

    /**
     * Id of the last video opened.  Includes Shorts.
     *
     * @return The id of the video, or an empty string if no videos have been opened yet.
     */
    @NonNull
    public static String getVideoId() {
        return videoId;
    }

    /**
     * Channel Name of the last video opened.  Includes Shorts.
     *
     * @return The channel name of the video.
     */
    @NonNull
    public static String getChannelName() {
        return channelName;
    }

    /**
     * ChannelId of the last video opened.  Includes Shorts.
     *
     * @return The channel id of the video.
     */
    @NonNull
    public static String getChannelId() {
        return channelId;
    }

    public static boolean getLiveStreamState() {
        return videoIsLiveStream;
    }


    /**
     * Differs from {@link #videoId} as this is the video id for the
     * last player response received, which may not be the last video opened.
     * <p>
     * If Shorts are loading the background, this commonly will be
     * different from the Short that is currently on screen.
     * <p>
     * For most use cases, you should instead use {@link #getVideoId()}.
     *
     * @return The id of the last video loaded, or an empty string if no videos have been loaded yet.
     */
    @NonNull
    public static String getPlayerResponseVideoId() {
        return playerResponseVideoId;
    }


    /**
     * @return If the last player response video id was a Short.
     * Includes Shorts shelf items appearing in the feed that are not opened.
     * @see #lastVideoIdIsShort()
     */
    public static boolean lastPlayerResponseIsShort() {
        return playerResponseVideoIdIsShort;
    }

    /**
     * @return If the last player response video id _that was opened_ was a Short.
     */
    public static boolean lastVideoIdIsShort() {
        return videoIdIsShort;
    }

    /**
     * @return If the player parameters are for a Short.
     */
    public static boolean playerParametersAreShort(@Nullable String playerParameter) {
        return playerParameter != null && playerParameter.startsWith(SHORTS_PLAYER_PARAMETERS);
    }

    /**
     * Injection point.
     */
    @Nullable
    public static String newPlayerResponseParameter(@NonNull String videoId, @Nullable String playerParameter, boolean isShortAndOpeningOrPlaying) {
        final boolean isShort = playerParametersAreShort(playerParameter);
        playerResponseVideoIdIsShort = isShort;
        if (!isShort || isShortAndOpeningOrPlaying) {
            if (videoIdIsShort != isShort) {
                videoIdIsShort = isShort;
            }
        }
        return playerParameter; // Return the original value since we are observing and not modifying.
    }

    /**
     * Injection point.  Called off the main thread.
     *
     * @param videoId The id of the last video loaded.
     */
    public static void setPlayerResponseVideoId(@NonNull String videoId, boolean isShortAndOpeningOrPlaying) {
        if (!playerResponseVideoId.equals(videoId)) {
            playerResponseVideoId = videoId;
        }
    }

    /**
     * @return The current playback speed.
     */
    public static float getPlaybackSpeed() {
        return playbackSpeed;
    }

    /**
     * Injection point.
     *
     * @param newlyLoadedPlaybackSpeed The current playback speed.
     */
    public static void setPlaybackSpeed(float newlyLoadedPlaybackSpeed) {
        playbackSpeed = newlyLoadedPlaybackSpeed;
    }

    /**
     * @return The current video quality.
     */
    public static int getVideoQuality() {
        return videoQuality;
    }

    /**
     * @return The current video quality string.
     */
    public static String getVideoQualityString() {
        return videoQualityString;
    }

    /**
     * Injection point.
     *
     * @param newlyLoadedQuality The current video quality string.
     */
    public static void setVideoQuality(String newlyLoadedQuality) {
        if (newlyLoadedQuality == null) {
            return;
        }
        try {
            String splitVideoQuality;
            if (newlyLoadedQuality.contains("p")) {
                splitVideoQuality = newlyLoadedQuality.split("p")[0];
                videoQuality = Integer.parseInt(splitVideoQuality);
                videoQualityString = splitVideoQuality + "p";
            } else if (newlyLoadedQuality.contains("s")) {
                splitVideoQuality = newlyLoadedQuality.split("s")[0];
                videoQuality = Integer.parseInt(splitVideoQuality);
                videoQualityString = splitVideoQuality + "s";
            } else {
                videoQuality = DEFAULT_YOUTUBE_VIDEO_QUALITY;
                videoQualityString = DEFAULT_YOUTUBE_VIDEO_QUALITY_STRING;
            }
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * @return available video quality.
     */
    public static int getAvailableVideoQuality(int preferredQuality) {
        if (videoQualities != null) {
            int qualityToUse = videoQualities.get(0); // first element is automatic mode
            for (Integer quality : videoQualities) {
                if (quality <= preferredQuality && qualityToUse < quality) {
                    qualityToUse = quality;
                }
            }
            preferredQuality = qualityToUse;
        }
        return preferredQuality;
    }

    /**
     * Injection point.
     *
     * @param qualities Video qualities available, ordered from largest to smallest, with index 0 being the 'automatic' value of -2
     */
    public static void setVideoQualityList(Object[] qualities) {
        try {
            if (videoQualities == null || videoQualities.size() != qualities.length) {
                videoQualities = new ArrayList<>(qualities.length);
                for (Object streamQuality : qualities) {
                    for (Field field : streamQuality.getClass().getFields()) {
                        if (field.getType().isAssignableFrom(Integer.TYPE)
                                && field.getName().length() <= 2) {
                            videoQualities.add(field.getInt(streamQuality));
                        }
                    }
                }
                Logger.printDebug(() -> "videoQualities: " + videoQualities);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "Failed to set quality list", ex);
        }
    }

    /**
     * Length of the current video playing.  Includes Shorts.
     *
     * @return The length of the video in milliseconds.
     * If the video is not yet loaded, or if the video is playing in the background with no video visible,
     * then this returns zero.
     */
    public static long getVideoLength() {
        return videoLength;
    }

    /**
     * Playback time of the current video playing.  Includes Shorts.
     * <p>
     * Value will lag behind the actual playback time by a variable amount based on the playback speed.
     * <p>
     * If playback speed is 2.0x, this value may be up to 2000ms behind the actual playback time.
     * If playback speed is 1.0x, this value may be up to 1000ms behind the actual playback time.
     * If playback speed is 0.5x, this value may be up to 500ms behind the actual playback time.
     * Etc.
     *
     * @return The time of the video in milliseconds. -1 if not set yet.
     */
    public static long getVideoTime() {
        return videoTime;
    }

    /**
     * Injection point.
     * Called on the main thread every 100ms.
     *
     * @param time The current playback time of the video in milliseconds.
     */
    public static void setVideoTime(final long time) {
        videoTime = time;
        Logger.printDebug(() -> "setVideoTime: " + getFormattedTimeStamp(time));
    }

    /**
     * @return If the playback is at the end of the video.
     * <p>
     * If video is playing in the background with no video visible,
     * this always returns false (even if the video is actually at the end).
     * <p>
     * This is equivalent to checking for {@link VideoState#ENDED},
     * but can give a more up-to-date result for code calling from some hooks.
     * @see VideoState
     */
    public static boolean isAtEndOfVideo() {
        return videoTime >= videoLength && videoLength > 0;
    }

    /**
     * Overrides the current playback speed.
     * Rest of the implementation added by patch.
     */
    public static void overridePlaybackSpeed(float speedOverride) {
        Logger.printDebug(() -> "Overriding playback speed to: " + speedOverride);
    }

    /**
     * Overrides the current quality.
     * Rest of the implementation added by patch.
     */
    public static void overrideVideoQuality(int qualityOverride) {
        Logger.printDebug(() -> "Overriding video quality to: " + qualityOverride);
    }

    /**
     * Overrides the current video time by seeking.
     * Rest of the implementation added by patch.
     */
    public static boolean overrideVideoTime(final long seekTime) {
        // These instructions are ignored by patch.
        Logger.printDebug(() -> "Seeking to " + seekTime);
        return false;
    }

    /**
     * Overrides the current video time by seeking. (MDX player)
     * Rest of the implementation added by patch.
     */
    public static boolean overrideMDXVideoTime(final long seekTime) {
        // These instructions are ignored by patch.
        Logger.printDebug(() -> "Seeking to " + seekTime);
        return false;
    }
}
