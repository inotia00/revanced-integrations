package app.revanced.integrations.music.patches.player;

import static app.revanced.integrations.shared.utils.ResourceUtils.getLayoutIdentifier;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.hideViewUnderCondition;
import static app.revanced.integrations.shared.utils.Utils.showToastShort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Arrays;

import app.revanced.integrations.music.patches.utils.CheckMusicVideoPatch;
import app.revanced.integrations.music.settings.Settings;
import app.revanced.integrations.music.shared.VideoType;
import app.revanced.integrations.music.utils.VideoUtils;

@SuppressWarnings("unused")
public class PlayerPatch {
    @SuppressLint("StaticFieldLeak")
    public static View previousButton;
    @SuppressLint("StaticFieldLeak")
    public static View nextButton;

    public static boolean enableColorMatchPlayer() {
        return Settings.ENABLE_COLOR_MATCH_PLAYER.get();
    }

    public static boolean enableForceMinimizedPlayer(boolean original) {
        return Settings.ENABLE_FORCE_MINIMIZED_PLAYER.get() || original;
    }

    public static boolean enableMiniPlayerNextButton(boolean original) {
        return !Settings.ENABLE_MINI_PLAYER_NEXT_BUTTON.get() && original;
    }

    public static View[] getViewArray(View[] oldViewArray) {
        if (previousButton != null) {
            if (nextButton != null) {
                return getViewArray(getViewArray(oldViewArray, previousButton), nextButton);
            } else {
                return getViewArray(oldViewArray, previousButton);
            }
        } else {
            return oldViewArray;
        }
    }

    private static View[] getViewArray(View[] oldViewArray, View newView) {
        final int oldViewArrayLength = oldViewArray.length;

        View[] newViewArray = Arrays.copyOf(oldViewArray, oldViewArrayLength + 1);
        newViewArray[oldViewArrayLength] = newView;
        return newViewArray;
    }

    public static void setNextButton(View nextButtonView) {
        if (nextButtonView == null)
            return;

        hideViewUnderCondition(
                !Settings.ENABLE_MINI_PLAYER_NEXT_BUTTON.get(),
                nextButtonView
        );

        nextButtonView.setOnClickListener(PlayerPatch::setNextButtonOnClickListener);
    }

    // rest of the implementation added by patch.
    private static void setNextButtonOnClickListener(View view) {
        if (Settings.ENABLE_MINI_PLAYER_NEXT_BUTTON.get())
            view.getClass();
    }

    public static void setPreviousButton(View previousButtonView) {
        if (previousButtonView == null)
            return;

        hideViewUnderCondition(
                !Settings.ENABLE_MINI_PLAYER_PREVIOUS_BUTTON.get(),
                previousButtonView
        );

        previousButtonView.setOnClickListener(PlayerPatch::setPreviousButtonOnClickListener);
    }

    // rest of the implementation added by patch.
    private static void setPreviousButtonOnClickListener(View view) {
        if (Settings.ENABLE_MINI_PLAYER_PREVIOUS_BUTTON.get())
            view.getClass();
    }

    public static boolean enableSwipeToDismissMiniPlayer() {
        return Settings.ENABLE_SWIPE_TO_DISMISS_MINI_PLAYER.get();
    }

    public static boolean enableSwipeToDismissMiniPlayer(boolean original) {
        return !Settings.ENABLE_SWIPE_TO_DISMISS_MINI_PLAYER.get() && original;
    }

    public static Object enableSwipeToDismissMiniPlayer(Object object) {
        return Settings.ENABLE_SWIPE_TO_DISMISS_MINI_PLAYER.get() ? null : object;
    }

    private static final int MUSIC_VIDEO_GREY_BACKGROUND_COLOR = -12566464;
    private static final int MUSIC_VIDEO_ORIGINAL_BACKGROUND_COLOR = -16579837;

    public static int enableZenMode(int originalColor) {
        if (Settings.ENABLE_ZEN_MODE.get() && originalColor == MUSIC_VIDEO_ORIGINAL_BACKGROUND_COLOR) {
            if (Settings.ENABLE_ZEN_MODE_PODCAST.get() || !VideoType.getCurrent().isPodCast()) {
                return MUSIC_VIDEO_GREY_BACKGROUND_COLOR;
            }
        }
        return originalColor;
    }

    public static void hideAudioVideoSwitchToggle(View view) {
        if (!Settings.HIDE_AUDIO_VIDEO_SWITCH_TOGGLE.get())
            return;

        if (view.getParent() instanceof ViewGroup viewGroup) {
            viewGroup.removeView(view);
        }
    }

    public static int hideFullscreenShareButton(int original) {
        return Settings.HIDE_FULLSCREEN_SHARE_BUTTON.get() ? 0 : original;
    }

    public static void setShuffleState(int buttonState) {
        if (!Settings.REMEMBER_SHUFFLE_SATE.get())
            return;
        Settings.SHUFFLE_SATE.save(buttonState);
    }

    public static int getShuffleState() {
        return Settings.SHUFFLE_SATE.get();
    }

    public static boolean rememberRepeatState(boolean original) {
        return Settings.REMEMBER_REPEAT_SATE.get() || original;
    }

    public static boolean rememberShuffleState() {
        return Settings.REMEMBER_SHUFFLE_SATE.get();
    }

    public static boolean restoreOldCommentsPopUpPanels(boolean original) {
        if (!Settings.SETTINGS_INITIALIZED.get()) {
            return original;
        }
        return !Settings.RESTORE_OLD_COMMENTS_POPUP_PANELS.get();
    }

    public static boolean restoreOldPlayerBackground(boolean original) {
        if (!Settings.SETTINGS_INITIALIZED.get()) {
            return original;
        }
        return !Settings.RESTORE_OLD_PLAYER_BACKGROUND.get();
    }

    public static boolean restoreOldPlayerLayout(boolean original) {
        if (!Settings.SETTINGS_INITIALIZED.get()) {
            return original;
        }
        return !Settings.RESTORE_OLD_PLAYER_LAYOUT.get();
    }

    private static void prepareOpenMusic() {
        if (!VideoType.getCurrent().isMusicVideo()) {
            showToastShort(str("revanced_replace_player_cast_button_playlist_dismiss"));
            return;
        }
        final String songId = CheckMusicVideoPatch.getSongId();
        if (songId.isEmpty()) {
            showToastShort(str("revanced_replace_player_cast_button_playlist_error"));
            return;
        }
        VideoUtils.openInYouTubeMusic(songId);
    }

    public static void replaceCastButton(Activity activity, ViewGroup viewGroup, View originalView) {
        if (!Settings.REPLACE_PLAYER_CAST_BUTTON.get()) {
            viewGroup.addView(originalView);
            return;
        }

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(activity).inflate(getLayoutIdentifier("open_music_button"), null);
        ImageView musicButtonView = (ImageView) linearLayout.getChildAt(0);

        musicButtonView.setOnClickListener(imageView -> prepareOpenMusic());

        viewGroup.addView(linearLayout);
    }
}
