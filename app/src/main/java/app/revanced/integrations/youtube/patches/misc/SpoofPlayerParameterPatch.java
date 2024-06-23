package app.revanced.integrations.youtube.patches.misc;

import static app.revanced.integrations.shared.utils.Utils.containsAny;
import static app.revanced.integrations.shared.utils.Utils.submitOnBackgroundThread;
import static app.revanced.integrations.youtube.patches.misc.requests.StoryboardRendererRequester.getStoryboardRenderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.PlayerType;
import app.revanced.integrations.youtube.shared.VideoInformation;

/**
 * @noinspection ALL
 * <p>
 * Even if user spoof any player parameters with the client name "ANDROID", if a valid DroidGuard result is not sent,
 * user always receive a response with video id 'aQvGIIdgFDM' (the following content is not available on this app).
 * <a href="https://github.com/LuanRT/YouTube.js/issues/623#issuecomment-2028586357">YouTube.js#623</a>
 * Therefore, this patch is no longer valid.
 * <p>
 * Currently, the only client name available on Android without DroidGuard results is "ANDROID_TESTSUITE".
 * <a href="https://github.com/iv-org/invidious/pull/4650">invidious#4650</a>
 */
@Deprecated
public class SpoofPlayerParameterPatch {
    private static final boolean spoofParameter = Settings.SPOOF_PLAYER_PARAMETER.get();
    private static final boolean spoofParameterInFeed = Settings.SPOOF_PLAYER_PARAMETER_IN_FEED.get();

    /**
     * Parameter (also used by
     * <a href="https://github.com/LuanRT/YouTube.js/pull/624">YouTube.js</a>)
     * to fix playback issues.
     */
    private static final String INCOGNITO_PARAMETERS = "CgIIAQ%3D%3D";

    /**
     * Parameters used when playing clips.
     */
    private static final String CLIPS_PARAMETERS = "kAIB";

    /**
     * Parameters causing playback issues.
     */
    private static final String[] AUTOPLAY_PARAMETERS = {
            "YAHI", // Autoplay in feed.
            "SAFg"  // Autoplay in scrim.
    };

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
    private static volatile Future<StoryboardRenderer> rendererFuture;

    private static volatile boolean useOriginalStoryboardRenderer;

    @Nullable
    private static StoryboardRenderer getRenderer(boolean waitForCompletion) {
        Future<StoryboardRenderer> future = rendererFuture;
        if (future != null) {
            try {
                if (waitForCompletion || future.isDone()) {
                    return future.get(20000, TimeUnit.MILLISECONDS); // Any arbitrarily large timeout.
                } // else, return null.
            } catch (TimeoutException ex) {
                Logger.printDebug(() -> "Could not get renderer (get timed out)");
            } catch (ExecutionException | InterruptedException ex) {
                // Should never happen.
                Logger.printException(() -> "Could not get renderer", ex);
            }
        }
        return null;
    }

    /**
     * Injection point.
     * <p>
     * {@link VideoInformation#getVideoId()} cannot be used because it is injected after PlayerResponse.
     * Therefore, we use the videoId called from PlaybackStartDescriptor.
     *
     * @param videoId    Original video id value.
     * @param parameters Original player parameter value.
     */
    public static String spoofParameter(@NonNull String videoId, @Nullable String parameters, boolean isShortAndOpeningOrPlaying) {
        try {
            Logger.printDebug(() -> "Original player parameter value: " + parameters);

            if (!spoofParameter) {
                return parameters;
            }

            // Shorts do not need to be spoofed.
            if (useOriginalStoryboardRenderer = VideoInformation.playerParametersAreShort(parameters)) {
                return parameters;
            }

            // Clip's player parameters contain important information such as where the video starts, where it ends, and whether it loops.
            // Clips are 60 seconds or less in length, so no spoofing.
            if (useOriginalStoryboardRenderer = parameters.length() > 150 || parameters.startsWith(CLIPS_PARAMETERS)) {
                return parameters;
            }

            final boolean isPlayingFeed = PlayerType.getCurrent() == PlayerType.INLINE_MINIMAL
                    && containsAny(parameters, AUTOPLAY_PARAMETERS);
            if (isPlayingFeed) {
                //noinspection AssignmentUsedAsCondition
                if (useOriginalStoryboardRenderer = !spoofParameterInFeed) {
                    // Don't spoof the feed video playback. This will cause video playback issues,
                    // but only if user continues watching for more than 1 minute.
                    return parameters;
                }
                // Spoof the feed video.  Video will show up in watch history and video subtitles are missing.
                fetchStoryboardRenderer(videoId);
                return SCRIM_PARAMETER + INCOGNITO_PARAMETERS;
            }

            fetchStoryboardRenderer(videoId);
        } catch (Exception ex) {
            Logger.printException(() -> "spoofParameter failure", ex);
        }
        return INCOGNITO_PARAMETERS;
    }

    private static void fetchStoryboardRenderer(@NonNull String videoId) {
        if (!videoId.equals(lastPlayerResponseVideoId)) {
            rendererFuture = submitOnBackgroundThread(() -> getStoryboardRenderer(videoId));
            lastPlayerResponseVideoId = videoId;
        }
        // Block until the renderer fetch completes.
        // This is desired because if this returns without finishing the fetch
        // then video will start playback but the storyboard is not ready yet.
        getRenderer(true);
    }

    private static String getStoryboardRendererSpec(String originalStoryboardRendererSpec,
                                                    boolean returnNullIfLiveStream) {
        if (spoofParameter && !useOriginalStoryboardRenderer) {
            final StoryboardRenderer renderer = getRenderer(false);
            if (renderer != null) {
                if (returnNullIfLiveStream && renderer.isLiveStream) {
                    return null;
                }
                String spec = renderer.spec;
                if (spec != null) {
                    return spec;
                }
            }
        }

        return originalStoryboardRendererSpec;
    }

    /**
     * Injection point.
     * Called from background threads and from the main thread.
     */
    @Nullable
    public static String getStoryboardRendererSpec(String originalStoryboardRendererSpec) {
        return getStoryboardRendererSpec(originalStoryboardRendererSpec, false);
    }

    /**
     * Injection point.
     * Uses additional check to handle live streams.
     * Called from background threads and from the main thread.
     */
    @Nullable
    public static String getStoryboardDecoderRendererSpec(String originalStoryboardRendererSpec) {
        return getStoryboardRendererSpec(originalStoryboardRendererSpec, true);
    }

    /**
     * Injection point.
     */
    public static int getStoryboardRecommendedLevel(int originalLevel) {
        if (spoofParameter && !useOriginalStoryboardRenderer) {
            final StoryboardRenderer renderer = getRenderer(false);
            if (renderer != null) {
                Integer recommendedLevel = renderer.recommendedLevel;
                if (recommendedLevel != null) return recommendedLevel;
            }
        }

        return originalLevel;
    }

    /**
     * Injection point.  Forces seekbar to be shown for paid videos or
     * if {@link Settings#SPOOF_PLAYER_PARAMETER} is not enabled.
     */
    public static boolean getSeekbarThumbnailOverrideValue() {
        if (!spoofParameter) {
            return false;
        }
        final StoryboardRenderer renderer = getRenderer(false);
        if (renderer == null) {
            // Spoof storyboard renderer is turned off,
            // video is paid, or the storyboard fetch timed out.
            // Show empty thumbnails so the seek time and chapters still show up.
            return true;
        }
        return renderer.spec != null;
    }
}
