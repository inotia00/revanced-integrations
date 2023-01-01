package app.revanced.integrations.patches.misc;

import android.view.WindowManager;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.swipecontrols.SwipeControlsHostActivity;

/**
 * Patch class for 'hdr-auto-brightness' patch
 *
 * @usedBy app/revanced/patches/youtube/misc/hdrbrightness/patch/HDRBrightnessPatch
 * @smali app/revanced/integrations/patches/misc/HDRAutoBrightnessPatch
 */
public class HDRAutoBrightnessPatch {
    private static String currentVideoId;
    private static boolean isHDRVideo = false;

    public static void newVideoStarted(final String videoId) {
        if (videoId == null || videoId.equals(currentVideoId)) return;

        currentVideoId = videoId;

        isHDRVideo = false;
    }

    public static float getHDRBrightness(float original) {

        isHDRVideo = true;

        if (!SettingsEnum.ENABLE_HDR_AUTO_BRIGHTNESS.getBoolean()) return original;

        // override with brightness set by swipe-controls
        // only when swipe-controls is active and has overridden the brightness
        final SwipeControlsHostActivity swipeControlsHost = SwipeControlsHostActivity.getCurrentHost().get();

        if (swipeControlsHost != null
                && swipeControlsHost.getScreen() != null
                && swipeControlsHost.getConfig().getEnableBrightnessControl()
                && !swipeControlsHost.getScreen().isDefaultBrightness()) {
            return swipeControlsHost.getScreen().getRawScreenBrightness();
        }

        // otherwise, set the brightness to auto
        return WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
    }

    public static boolean getHDRVideo() {
        return isHDRVideo;
    }
}
