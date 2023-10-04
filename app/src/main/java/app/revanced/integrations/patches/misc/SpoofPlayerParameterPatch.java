package app.revanced.integrations.patches.misc;

import static app.revanced.integrations.patches.misc.requests.StoryBoardRendererRequester.getStoryboardRenderer;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.shared.PlayerType;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedUtils;

/**
 * @noinspection ALL
 */
public class SpoofPlayerParameterPatch {

    /**
     * Parameter (also used by
     * <a href="https://github.com/yt-dlp/yt-dlp/blob/81ca451480051d7ce1a31c017e005358345a9149/yt_dlp/extractor/youtube.py#L3602">yt-dlp</a>)
     * to fix playback issues.
     */
    private static final String INCOGNITO_PARAMETERS = "CgIQBg==";

    /**
     * I found this parameter when I (Alireza) openned
     * a video from Notifications. Luckily, it
     * worked without any side effect.
     * 
     * Also this params may be fixed by Google,
     * so we can remove it anytime.
     */
    private static final String NOTIFICATIONS_PARAMETERS = "wGIGCgQQAhgC";

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
     * Parameters used in YouTube Shorts.
     */
    private static final String SHORTS_PLAYER_PARAMETERS = "8AEB";

    /**
     * Last video id loaded. Used to prevent reloading the same spec multiple times.
     */
    private static volatile String lastPlayerResponseVideoId;

    private static volatile Future<StoryboardRenderer> rendererFuture;

    private static volatile boolean originalStoryboardRenderer;


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
        if (originalStoryboardRenderer = parameters.startsWith(SHORTS_PLAYER_PARAMETERS)) {
            return parameters;
        }

        // Clip's player parameters contain important information such as where the video starts, where it ends, and whether it loops.
        // For this reason, the player parameters of a clip are usually very long (150~300 characters).
        // Clips are 60 seconds or less in length, so no spoofing.
        if (originalStoryboardRenderer = parameters.length() > 150) {
            return parameters;
        }

        if (originalStoryboardRenderer = PlayerType.getCurrent() == PlayerType.INLINE_MINIMAL
                && AUTOPLAY_PARAMETERS.stream().anyMatch(parameters::contains)) {
            // In order to prevent videos that are auto-played in feed to be added to history,
            // only spoof the parameter if the video is not playing in the feed.
            // This will cause playback issues in the feed, but it's better than manipulating the history.
            return SCRIM_PARAMETER + SHORTS_PLAYER_PARAMETERS;
        } else {
            // StoryboardRenderer is always empty when playing video with INCOGNITO_PARAMETERS and NOTIFICATIONS_PARAMETERS parameter.
            // Fetch StoryboardRenderer without parameter.
            if (SettingsEnum.SPOOF_PLAYER_PARAMETER_TYPE.getBoolean()) fetchStoryboardRenderer(videoId);

            // If Turned on Spoof Player Parameter Type, it will use Incognito Mode
            final String playerParameters = SettingsEnum.SPOOF_PLAYER_PARAMETER_TYPE.getBoolean()
                ? INCOGNITO_PARAMETERS
                : NOTIFICATIONS_PARAMETERS;

            // Spoof the player parameter to prevent playback issues.
            return playerParameters;
        }
    }

    private static void fetchStoryboardRenderer(String videoId) {
        if (!videoId.equals(lastPlayerResponseVideoId)) {
            rendererFuture = ReVancedUtils.submitOnBackgroundThread(() -> getStoryboardRenderer(videoId));
            lastPlayerResponseVideoId = videoId;
        }
        // Block until the fetch is completed.  Without this, occasionally when a new video is opened
        // the video will be frozen a few seconds while the audio plays.
        // This is because the main thread is calling to get the storyboard but the fetch is not completed.
        // To prevent this, call get() here and block until the fetch is completed.
        // So later when the main thread calls to get the renderer it will never block as the future is done.
        getRenderer();
    }

    @Nullable
    private static StoryboardRenderer getRenderer() {
        if (rendererFuture != null) {
            try {
                return rendererFuture.get(5000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                LogHelper.printDebug(SpoofPlayerParameterPatch.class, "Could not get renderer (get timed out)");
            } catch (ExecutionException | InterruptedException ex) {
                // Should never happen.
                LogHelper.printException(SpoofPlayerParameterPatch.class, "Could not get renderer", ex);
            }
        }
        return null;
    }

    /**
     * Injection point.
     */
    public static boolean getSeekbarThumbnailOverrideValue() {
        return SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean();
    }

    /**
     * Injection point.
     * Called from background threads and from the main thread.
     */
    @Nullable
    public static String getStoryboardRendererSpec() {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer|| !SettingsEnum.SPOOF_PLAYER_PARAMETER_TYPE.getBoolean())
            return null;

        StoryboardRenderer renderer = getRenderer();
        if (renderer != null)
            return renderer.spec();

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
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer|| !SettingsEnum.SPOOF_PLAYER_PARAMETER_TYPE.getBoolean())
            return originalStoryboardRendererSpec;

        StoryboardRenderer renderer = getRenderer();
        if (renderer != null) {
            return renderer.isLiveStream() ? null : renderer.spec();
        }

        return originalStoryboardRendererSpec;
    }

    /**
     * Injection point.
     */
    public static int getRecommendedLevel(int originalLevel) {
        if (!SettingsEnum.SPOOF_PLAYER_PARAMETER.getBoolean() || originalStoryboardRenderer|| !SettingsEnum.SPOOF_PLAYER_PARAMETER_TYPE.getBoolean())
            return originalLevel;

        StoryboardRenderer renderer = getRenderer();
        if (renderer != null) {
            Integer recommendedLevel = renderer.recommendedLevel();
            if (recommendedLevel != null)
                return recommendedLevel;
        }

        return originalLevel;
    }
}
