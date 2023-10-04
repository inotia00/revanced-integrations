package app.revanced.integrations.patches.layout;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ResourceHelper;

public class PlayerPatch {
    @SuppressLint("StaticFieldLeak")
    private static ViewGroup coreContainer;

    public static boolean disableChapterVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_CHAPTERS.getBoolean();
    }

    public static boolean disableSeekVibrate() {
        return SettingsEnum.DISABLE_HAPTIC_FEEDBACK_SEEK.getBoolean();
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

    public static void hideCaptionsButton(ImageView imageView) {
        imageView.setVisibility(SettingsEnum.HIDE_CAPTIONS_BUTTON.getBoolean() ? ImageView.GONE : ImageView.VISIBLE);
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

    public static void hidePlayerOverlayFilter(ImageView view) {
        if (!SettingsEnum.HIDE_PLAYER_OVERLAY_FILTER.getBoolean()) return;
        view.setImageResource(android.R.color.transparent);
    }

    public static boolean hideSeekMessage() {
        return SettingsEnum.HIDE_SEEK_MESSAGE.getBoolean();
    }

    public static void hideSuggestedVideoOverlay(ViewGroup viewGroup) {
        if (!SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY.getBoolean())
            return;

        if (coreContainer != viewGroup)
            coreContainer = viewGroup;

        viewGroup.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

            // Text Container View [R.id.text_container] is placed on the 1st ChildView.
            if (viewGroup.getChildCount() < 1 || !(viewGroup.getChildAt(0) instanceof LinearLayout textContainer))
                return;

            // Close Button [R.id.close_button] is placed on the 2nd ChildView.
            if (textContainer.getChildCount() < 2 || !(textContainer.getChildAt(1) instanceof ImageView imageView))
                return;

            // Click the Close Button.
            imageView.performClick();
        });
    }

    public static void hideSuggestedVideoOverlay() {
        if (!SettingsEnum.HIDE_SUGGESTED_VIDEO_OVERLAY.getBoolean() || coreContainer == null)
            return;

        try {
            ((LinearLayout) coreContainer.getChildAt(0)).getChildAt(1).performClick();
        } catch (Exception ignored) {
        }
    }
}
