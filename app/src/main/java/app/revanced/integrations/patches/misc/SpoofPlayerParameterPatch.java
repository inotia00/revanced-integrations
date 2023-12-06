package app.revanced.integrations.patches.misc;

import static app.revanced.integrations.patches.misc.requests.StoryBoardRendererRequester.getStoryboardRenderer;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.shared.PlayerType;
import app.revanced.integrations.utils.LogHelper;

/**
 * @noinspection ALL
 */
public class SpoofPlayerParameterPatch {
    /** 
     * Parameters used in Your Clips. 
     */ 
    private static final String CLIPS_PLAYER_PARAMETERS = "kAIB";
    
    /**
     * Parameter (also used by
     * <a href="https://github.com/yt-dlp/yt-dlp/blob/81ca451480051d7ce1a31c017e005358345a9149/yt_dlp/extractor/youtube.py#L3602">yt-dlp</a>)
     * to fix playback issues.
     */
    private static final String INCOGNITO_PARAMETERS = "CgIQBg==";

    /**
     * Parameters causing playback issues.
     */
    private static final List<String> AUTOPLAY_PARAMETERS = Arrays.asList(
            "YAHI", // Autoplay in feed
            "SAFg"  // Autoplay in scrim
    );

    /**
     * Parameter used for autoplay in scrim.
     * Prepend this parameter to mute video playback (for autoplay in feed).
     */
    private static final String SCRIM_PARAMETER = "SAFgAXgB";

    /**
     * Last video id loaded. Used to prevent reloading the same spec multiple times.
     */
    @Nullable
    private static volatile String lastPlayerResponseVideoId;

    @Nullable
    private static volatile StoryboardRenderer videoRenderer;

    private static volatile boolean originalStoryboardRenderer;


    /**
     * Injection point.
     * <p>
     * Called off the main thread, and called multiple times for each video.
     *
     * @param parameters Original player parameter value.
     */
    public static String spoofParameter(String videoId, String parameters, boolean isShortAndOpeningOrPlaying) {
        LogHelper.printDebug(() -> "Original player parameter value: " + parameters);

        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean()) {
            return parameters;
        }

        // Shorts do not need to be spoofed.
        if (originalStoryboardRenderer = VideoInformation.playerParametersAreShort(parameters)) {
            return parameters;
        }

        // Clip's player parameters contain important information such as where the video starts, where it ends, and whether it loops.
        // For this reason, the player parameters of a clip are usually very long (150~300 characters).
        // Clips are 60 seconds or less in length, so no spoofing.
        final boolean isClip = parameters.length() > 150  
                     || parameters.startsWith(CLIPS_PLAYER_PARAMETERS) 
        if (originalStoryboardRenderer = isClip) {
            return parameters;
        }

        final boolean isPlayingFeed = PlayerType.getCurrent() == PlayerType.INLINE_MINIMAL
                && AUTOPLAY_PARAMETERS.stream().anyMatch(parameters::contains);

        if (!isPlayingFeed) {
            // StoryboardRenderer is always empty when playing video with INCOGNITO_PARAMETERS parameter.
            // Fetch StoryboardRenderer without parameter.
            fetchStoryboardRenderer(videoId);
            // Spoof the player parameter to prevent playback issues.
            return INCOGNITO_PARAMETERS;
        }

        if (originalStoryboardRenderer = !SettingsEnum.SPOOF_PLAYER_PARAMETER_IN_FEED.getBoolean()) {
            return parameters;
        } else {
            // StoryboardRenderer is always empty when playing video with INCOGNITO_PARAMETERS parameter.
            // Fetch StoryboardRenderer without parameter.
            fetchStoryboardRenderer(videoId);
            // Spoof the player parameter to prevent playback issues.
            return SCRIM_PARAMETER + INCOGNITO_PARAMETERS;
        }
    }

    private static void fetchStoryboardRenderer(String videoId) {
        if (!videoId.equals(lastPlayerResponseVideoId)) {
            lastPlayerResponseVideoId = videoId;
            // This will block starting video playback until the fetch completes.
            // This is desired because if this returns without finishing the fetch,
            // then video will start playback but the image will be frozen
            // while the main thread call for the renderer waits for the fetch to complete.
            videoRenderer = getStoryboardRenderer(videoId);
        }
    }

    /**
     * Injection point.  Forces seekbar to be shown for paid videos or
     * if {@link SettingsEnum#SPOOF_PLAYER_PARAMETER} is not enabled.
     */
    public static boolean getSeekbarThumbnailOverrideValue() {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean()) {
            return false;
        }
        final StoryboardRenderer renderer = videoRenderer;
        // Spoof storyboard renderer is turned off,
        // video is paid, or the storyboard fetch timed out.
        // Show empty thumbnails so the seek time and chapters still show up.
        return renderer == null || renderer.getSpec() != null;
    }

    /**
     * Injection point.
     * Called from background threads and from the main thread.
     */
    @Nullable
    public static String getStoryboardRendererSpec() {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer)
            return null;

        StoryboardRenderer renderer = videoRenderer;
        if (renderer != null)
            return renderer.getSpec();

        return null;
    }

    /**
     * Injection point.
     * <p>
     * This method is only injected into methods that create storyboards in the live stream.
     * In this case, this value should be null in the live stream.
     */
    @Nullable
    public static String getStoryboardRendererSpec(String originalStoryboardRendererSpec) {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer)
            return originalStoryboardRendererSpec;

        StoryboardRenderer renderer = videoRenderer;
        if (renderer != null) {
            return renderer.isLiveStream() ? null : renderer.getSpec();
        }

        return originalStoryboardRendererSpec;
    }

    /**
     * Injection point.
     */
    public static int getRecommendedLevel(int originalLevel) {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer)
            return originalLevel;

        StoryboardRenderer renderer = videoRenderer;
        if (renderer != null) {
            Integer recommendedLevel = renderer.getRecommendedLevel();
            if (recommendedLevel != null)
                return recommendedLevel;
        }

        return originalLevel;
    }
}
