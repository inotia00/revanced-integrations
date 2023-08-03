package app.revanced.integrations.patches.misc;

import java.util.Arrays;
import java.util.List;

import app.revanced.integrations.patches.misc.requests.StoryBoardRendererRequester;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.shared.PlayerType;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.whitelist.Whitelist;

public class SpoofPlayerParameterPatch {

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
     * Parameters used in YouTube Shorts.
     */
    private static final String SHORTS_PLAYER_PARAMETERS = "8AEB";

    private static String storyboardRendererSpec = "";


    /**
     * Injection point.
     * <p>
     * {@link VideoInformation#getVideoId()} cannot be used because it is injected after PlayerResponse.
     * Therefore, we use the videoId called from PlaybackStartDescriptor.
     *
     * @param videoId    Original video id value.
     * @param parameters Original player parameter value.
     */
    public static String spoofParameter(String videoId, String parameters) {
        LogHelper.printDebug(SpoofPlayerParameterPatch.class, "Original player parameter value: " + parameters);

        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean()) {
            return parameters;
        }

        // Shorts do not need to be spoofed.
        if (parameters.startsWith(SHORTS_PLAYER_PARAMETERS)) {
            return parameters;
        }

        // Video with Ads don't need to be spoofed.
        if (Whitelist.isChannelADSWhitelisted()) {
            return parameters;
        }

        // Clip's player parameters contain important information such as where the video starts, where it ends, and whether it loops.
        // For this reason, the player parameters of a clip are usually very long (150~300 characters).
        // Clips are 60 seconds or less in length, so no spoofing.
        if (parameters.length() > 150) {
            return parameters;
        }

        final boolean isPlayingFeed = PlayerType.getCurrent() == PlayerType.INLINE_MINIMAL
                && AUTOPLAY_PARAMETERS.stream().anyMatch(parameters::contains);

        if (isPlayingFeed) {
            // In order to prevent videos that are auto-played in feed to be added to history,
            // only spoof the parameter if the video is not playing in the feed.
            // This will cause playback issues in the feed, but it's better than manipulating the history.
            return parameters;
        } else {
            // StoryboardRenderer is always empty when playing video with INCOGNITO_PARAMETERS parameter.
            // Fetch StoryboardRenderer without parameter.
            StoryBoardRendererRequester.fetchStoryboardsRenderer(videoId);
            // Spoof the player parameter to prevent playback issues.
            return INCOGNITO_PARAMETERS;
        }
    }

    /**
     * Injection point.
     */
    public static boolean getSeekbarThumbnailOverrideValue() {
        return SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean();
    }

    /**
     * Injection point.
     */
    public static String getStoryboardRendererSpec() {
        return storyboardRendererSpec;
    }

    public static void setStoryboardRendererSpec(String newlyLoadedStoryboardRendererSpec) {
        if (storyboardRendererSpec.equals(newlyLoadedStoryboardRendererSpec))
            return;

        storyboardRendererSpec = newlyLoadedStoryboardRendererSpec;
        LogHelper.printDebug(SpoofPlayerParameterPatch.class, "New StoryBoard Renderer Spec Loaded: " + newlyLoadedStoryboardRendererSpec);
    }
}
