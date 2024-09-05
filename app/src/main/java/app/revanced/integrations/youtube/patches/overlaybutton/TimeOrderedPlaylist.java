package app.revanced.integrations.youtube.patches.overlaybutton;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class TimeOrderedPlaylist extends BottomControlButton {
    @Nullable
    private static TimeOrderedPlaylist instance;

    public TimeOrderedPlaylist(ViewGroup bottomControlsViewGroup) {
        super(
                bottomControlsViewGroup,
                "time_ordered_playlist_button",
                Settings.OVERLAY_BUTTON_TIME_ORDERED_PLAYLIST,
                view -> VideoUtils.openVideo(true),
                view -> {
                    VideoUtils.openVideo(false);
                    return true;
                }
        );
    }

    /**
     * Injection point.
     */
    public static void initialize(View bottomControlsViewGroup) {
        try {
            if (bottomControlsViewGroup instanceof ViewGroup viewGroup) {
                instance = new TimeOrderedPlaylist(viewGroup);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    /**
     * Injection point.
     */
    public static void changeVisibility(boolean showing, boolean animation) {
        if (instance != null) instance.setVisibility(showing, animation);
    }

    public static void changeVisibilityNegatedImmediate() {
        if (instance != null) instance.setVisibilityNegatedImmediate();
    }

}