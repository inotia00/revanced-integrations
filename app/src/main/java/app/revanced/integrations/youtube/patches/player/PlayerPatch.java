package app.revanced.integrations.youtube.patches.player;

import static app.revanced.integrations.youtube.utils.StringRef.str;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import app.revanced.integrations.youtube.settings.SettingsEnum;
import app.revanced.integrations.youtube.utils.LogHelper;
import app.revanced.integrations.youtube.utils.ReVancedUtils;
import app.revanced.integrations.youtube.utils.ResourceHelper;

@SuppressWarnings("unused")
public class PlayerPatch {
    public static void changePlayerOpacity(ImageView imageView) {
        int opacity = SettingsEnum.CUSTOM_PLAYER_OVERLAY_OPACITY.getInt();

        if (opacity < 0 || opacity > 100) {
            ReVancedUtils.showToastShort(str("revanced_custom_player_overlay_opacity_warning"));
            SettingsEnum.CUSTOM_PLAYER_OVERLAY_OPACITY.resetToDefault();
            opacity = (int) SettingsEnum.CUSTOM_PLAYER_OVERLAY_OPACITY.defaultValue;
        }

        imageView.setImageAlpha((opacity * 255) / 100);
    }

    public static boolean disableSpeedOverlay(boolean original) {
        return !SettingsEnum.DISABLE_SPEED_OVERLAY.getBoolean() && original;
    }

    public static boolean disableChapterVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_CHAPTERS.getBoolean();
    }

    public static boolean disableSeekVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_SEEK.getBoolean();
    }

    public static boolean disableSeekUndoVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_SEEK_UNDO.getBoolean();
    }

    public static boolean disableScrubbingVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_SCRUBBING.getBoolean();
    }

    public static boolean disableZoomVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_ZOOM.getBoolean();
    }

    public static boolean hideAutoPlayButton() {
        return SettingsEnum.HIDE_AUTOPLAY_BUTTON.getBoolean();
    }

    public static boolean hideCaptionsButton(boolean original) {
        return !SettingsEnum.HIDE_CAPTIONS_BUTTON.getBoolean() && original;
    }

    public static void hideCaptionsButton(View view) {
        if (!SettingsEnum.HIDE_CAPTIONS_BUTTON.getBoolean())
            return;

        view.setVisibility(View.GONE);
        ReVancedUtils.hideViewByLayoutParams(view);
    }

    public static int hideCastButton(int original) {
        return SettingsEnum.HIDE_CAST_BUTTON.getBoolean() ? View.GONE : original;
    }

    public static boolean hideChannelWatermark() {
        return !SettingsEnum.HIDE_CHANNEL_WATERMARK.getBoolean();
    }

    public static void hideEndScreenCards(View view) {
        if (SettingsEnum.HIDE_END_SCREEN_CARDS.getBoolean()) {
            view.setVisibility(View.GONE);
        }
    }

    public static boolean hideFilmstripOverlay() {
        return SettingsEnum.HIDE_FILMSTRIP_OVERLAY.getBoolean();
    }

    public static boolean hideInfoCard(boolean original) {
        return !SettingsEnum.HIDE_INFO_CARDS.getBoolean() && original;
    }

    public static boolean hideMusicButton() {
        return SettingsEnum.HIDE_YOUTUBE_MUSIC_BUTTON.getBoolean();
    }

    public static int hidePlayerButton(View view, int originalValue) {
        return ResourceHelper.hidePlayerButton(view, originalValue);
    }

    public static boolean hidePreviousNextButton(boolean previousOrNextButtonVisible) {
        return !SettingsEnum.HIDE_PREVIOUS_NEXT_BUTTON.getBoolean() && previousOrNextButtonVisible;
    }

    public static boolean hideSeekMessage() {
        return SettingsEnum.HIDE_SEEK_MESSAGE.getBoolean();
    }

    public static boolean hideSeekUndoMessage() {
        return SettingsEnum.HIDE_SEEK_UNDO_MESSAGE.getBoolean();
    }

    public static void hideSuggestedVideoOverlay(View view) {
        if (!SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY.getBoolean() || !SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY_AUTO_PLAY.getBoolean())
            return;

        if (view != null) {
            view.setSoundEffectsEnabled(false);
            view.performClick();
        }
    }

    public static void hideSuggestedVideoOverlay(ViewGroup viewGroup) {
        if (!SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY.getBoolean() || SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY_AUTO_PLAY.getBoolean())
            return;

        viewGroup.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            try {
                final View closeButton = ((LinearLayout) viewGroup.getChildAt(0)).getChildAt(1);
                if (closeButton != null) {
                    closeButton.setSoundEffectsEnabled(false);
                    closeButton.performClick();
                }
            } catch (Exception ex) {
                LogHelper.printException(() -> "hideSuggestedVideoOverlay failure", ex);
            }
        });
    }
}
