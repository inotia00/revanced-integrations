package app.revanced.integrations.youtube.patches.utils;

import android.util.Log;

import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class AlwaysRepeatPatch {

    /**
     * Injection point.
     *
     * @return video is repeated.
     */
    public static boolean alwaysRepeat() {
        return alwaysRepeatEnabled() && VideoInformation.overrideVideoTime(0);
    }

    public static boolean alwaysRepeatEnabled() {
        final boolean alwaysRepeat = Settings.ALWAYS_REPEAT.get();
        final boolean alwaysRepeatPause = Settings.ALWAYS_REPEAT_PAUSE.get();

        if (alwaysRepeat && alwaysRepeatPause) pauseVideo();
        return alwaysRepeat;
    }

    /**
     * Pause the current video.
     * Rest of the implementation added by patch.
     */
    private static void pauseVideo() {
        // These instructions are ignored by patch.
        Log.d("Extended: AlwaysRepeatPatch", "AlwaysRepeatAndPauseState: " + Settings.ALWAYS_REPEAT_PAUSE.get());
    }

}
